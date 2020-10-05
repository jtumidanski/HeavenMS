package net.server.handlers.login;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import client.MapleClient;
import client.creator.CharacterFactory;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.CreateCharacterReader;
import net.server.login.packet.CreateCharacterPacket;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.PacketCreator;
import tools.packet.DeleteCharacter;
import tools.packet.DeleteCharacterResponse;

public final class CreateCharHandler extends AbstractPacketHandler<CreateCharacterPacket> {
   @Override
   public Class<CreateCharacterReader> getReaderClass() {
      return CreateCharacterReader.class;
   }

   @Override
   public void handlePacket(CreateCharacterPacket packet, MapleClient client) {
      int[] items = new int[]{packet.weapon(), packet.top(), packet.bottom(), packet.shoes(), packet.hair(), packet.face()};
      for (int item : items) {
         if (!isLegal(item)) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, "Owner from account '" + client.getAccountName() + "' tried to packet edit in char creation.");
            client.disconnect(true, false);
            return;
         }
      }

      int status;
      if (packet.job() == 0) { // Knights of Cygnus
         status = CharacterFactory.getInstance().createNoblesse(client, packet.name(), packet.face(), packet.hair() + packet.hairColor(), packet.skinColor(), packet.top(), packet.bottom(), packet.shoes(), packet.weapon(), packet.gender());
      } else if (packet.job() == 1) { // Adventurer
         status = CharacterFactory.getInstance().createBeginner(client, packet.name(), packet.face(), packet.hair() + packet.hairColor(), packet.skinColor(), packet.top(), packet.bottom(), packet.shoes(), packet.weapon(), packet.gender());
      } else if (packet.job() == 2) { // Aran
         status = CharacterFactory.getInstance().createLegend(client, packet.name(), packet.face(), packet.hair() + packet.hairColor(), packet.skinColor(), packet.top(), packet.bottom(), packet.shoes(), packet.weapon(), packet.gender());
      } else {
         PacketCreator.announce(client, new DeleteCharacter(0, DeleteCharacterResponse.UNKNOWN_ERROR));
         return;
      }

      if (status == -2) {
         PacketCreator.announce(client, new DeleteCharacter(0, DeleteCharacterResponse.UNKNOWN_ERROR));
      }
   }

   private final Set<Integer> IDs = new HashSet<>(Arrays.asList(1302000, 1312004, 1322005, 1442079,// weapons
         1040002, 1040006, 1040010, 1041002, 1041006, 1041010, 1041011, 1042167,// bottom
         1060002, 1060006, 1061002, 1061008, 1062115, // top
         1072001, 1072005, 1072037, 1072038, 1072383,// shoes
         30000, 30010, 30020, 30030, 31000, 31040, 31050,// hair
         20000, 20001, 20002, 21000, 21001, 21002, 21201, 20401, 20402, 21700, 20100  //face
         //#NeverTrustStevenCode
   ));

   private boolean isLegal(Integer toCompare) {
      return IDs.contains(toCompare);
   }
}