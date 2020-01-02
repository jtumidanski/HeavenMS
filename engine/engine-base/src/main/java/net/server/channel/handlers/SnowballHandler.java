package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.SnowballPacket;
import net.server.channel.packet.reader.SnowballReader;
import server.events.gm.MapleSnowball;
import server.maps.MapleMap;

public final class SnowballHandler extends AbstractPacketHandler<SnowballPacket> {
   @Override
   public Class<SnowballReader> getReaderClass() {
      return SnowballReader.class;
   }

   @Override
   public void handlePacket(SnowballPacket packet, MapleClient client) {
      //D3 00 02 00 00 A5 01
      MapleCharacter chr = client.getPlayer();
      MapleMap map = chr.getMap();
      final MapleSnowball snowball = map.getSnowball(chr.getTeam());
      final MapleSnowball otherSnowBall = map.getSnowball(chr.getTeam() == 0 ? (byte) 1 : 0);

      if (snowball == null || otherSnowBall == null || snowball.getSnowmanHP() == 0) {
         return;
      }
      if ((currentServerTime() - chr.getLastSnowballAttack()) < 500) {
         return;
      }
      if (chr.getTeam() != (packet.what() % 2)) {
         return;
      }

      chr.setLastSnowballAttack(currentServerTime());
      int damage = 0;
      if (packet.what() < 2 && otherSnowBall.getSnowmanHP() > 0) {
         damage = 10;
      } else if (packet.what() == 2 || packet.what() == 3) {
         if (Math.random() < 0.03) {
            damage = 45;
         } else {
            damage = 15;
         }
      }

      if (packet.what() >= 0 && packet.what() <= 4) {
         snowball.hit(packet.what(), damage);
      }

   }
}
