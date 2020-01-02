package net.server.handlers.login;

import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.ShowAllCharacter;
import tools.packet.ShowAllCharacterInfo;

public final class ViewAllCharHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      if (!client.canRequestCharacterList()) {
         PacketCreator.announce(client, new ShowAllCharacter(0, 0));
         return;
      }

      int accountId = client.getAccID();
      Pair<Pair<Integer, List<MapleCharacter>>, List<Pair<Integer, List<MapleCharacter>>>> loginBlob = Server.getInstance().loadAccountCharacterList(accountId, client.getVisibleWorlds());

      List<Pair<Integer, List<MapleCharacter>>> worldCharacters = loginBlob.getRight();
      int chrTotal = loginBlob.getLeft().getLeft();
      List<MapleCharacter> lastWorldCharacters = loginBlob.getLeft().getRight();

      if (chrTotal > 9) {
         int padRight = chrTotal % 3;
         if (padRight > 0 && lastWorldCharacters != null) {
            MapleCharacter chr = lastWorldCharacters.get(lastWorldCharacters.size() - 1);

            for (int i = padRight; i < 3; i++) { // filling the remaining slots with the last character loaded
               chrTotal++;
               lastWorldCharacters.add(chr);
            }
         }
      }

      int charsSize = chrTotal;
      int unk = charsSize + (3 - charsSize % 3); //rowSize?
      PacketCreator.announce(client, new ShowAllCharacter(charsSize, unk));

      for (Pair<Integer, List<MapleCharacter>> worldCharacter : worldCharacters) {
         PacketCreator.announce(client, new ShowAllCharacterInfo(worldCharacter.getLeft(), worldCharacter.getRight(), YamlConfig.config.server.ENABLE_PIC && client.cannotBypassPic()));
      }
   }
}
