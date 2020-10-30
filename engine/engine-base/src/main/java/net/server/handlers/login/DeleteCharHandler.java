package net.server.handlers.login;

import java.util.Optional;

import client.MapleClient;
import client.processor.CharacterProcessor;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.DeleteCharacterReader;
import net.server.login.packet.DeleteCharacterPacket;
import tools.PacketCreator;
import tools.packet.DeleteCharacter;
import tools.packet.DeleteCharacterResponse;

public final class DeleteCharHandler extends AbstractPacketHandler<DeleteCharacterPacket> {
   @Override
   public Class<DeleteCharacterReader> getReaderClass() {
      return DeleteCharacterReader.class;
   }

   @Override
   public void handlePacket(DeleteCharacterPacket packet, MapleClient client) {
      if (client.checkPic(packet.pic())) {
         //check for family, guild leader, pending marriage, world transfer
         Optional<Byte> state = CharacterProcessor.getInstance().canDeleteCharacter(packet.characterId());
         if (state.isPresent()) {
            PacketCreator.announce(client, new DeleteCharacter(packet.characterId(), DeleteCharacterResponse.fromValue(state.get())));
            return;
         }

         if (client.deleteCharacter(packet.characterId(), client.getAccID())) {
            LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.DELETED_CHAR, client.getAccountName() + " deleted CID: " + packet.characterId());
            PacketCreator.announce(client, new DeleteCharacter(packet.characterId(), DeleteCharacterResponse.SUCCESS));
         } else {
            PacketCreator.announce(client, new DeleteCharacter(packet.characterId(), DeleteCharacterResponse.UNKNOWN_ERROR));
         }
      } else {
         PacketCreator.announce(client, new DeleteCharacter(packet.characterId(), DeleteCharacterResponse.INCORRECT_PIC));
      }
   }
}
