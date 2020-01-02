package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.AutoAggroPacket;
import net.server.channel.packet.reader.AutoAggroReader;
import server.life.MapleMonster;
import server.maps.MapleMap;

public final class AutoAggroHandler extends AbstractPacketHandler<AutoAggroPacket> {
   @Override
   public Class<AutoAggroReader> getReaderClass() {
      return AutoAggroReader.class;
   }

   @Override
   public void handlePacket(AutoAggroPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player.isHidden()) {
         return; // Don't auto aggro GM's in hide...
      }

      MapleMap map = player.getMap();

      MapleMonster monster = map.getMonsterByOid(packet.objectId());
      if (monster != null) {
         monster.aggroAutoAggroUpdate(player);
      }
   }
}
