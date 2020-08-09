package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.CharacterInfoRequestPacket;
import net.server.channel.packet.reader.CharacterInfoRequestReader;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.character.GetCharacterInfo;

public final class CharInfoRequestHandler extends AbstractPacketHandler<CharacterInfoRequestPacket> {
   @Override
   public Class<CharacterInfoRequestReader> getReaderClass() {
      return CharacterInfoRequestReader.class;
   }

   @Override
   public void handlePacket(CharacterInfoRequestPacket packet, MapleClient client) {
      MapleMapObject target = client.getPlayer().getMap().getMapObject(packet.characterId());
      if (target != null) {
         if (target instanceof MapleCharacter player) {
            if (client.getPlayer().getId() != player.getId()) {
               player.exportExcludedItems(client);
            }
            PacketCreator.announce(client, new GetCharacterInfo(player));
         }
      }
   }
}
