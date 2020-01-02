package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.CoconutPacket;
import net.server.channel.packet.reader.CoconutReader;
import server.events.gm.MapleCoconut;
import server.events.gm.MapleCoconuts;
import server.maps.MapleMap;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.packet.event.CoconutHit;
import tools.packet.event.CoconutScore;

public final class CoconutHandler extends AbstractPacketHandler<CoconutPacket> {
   @Override
   public Class<CoconutReader> getReaderClass() {
      return CoconutReader.class;
   }

   @Override
   public void handlePacket(CoconutPacket packet, MapleClient client) {
      MapleMap map = client.getPlayer().getMap();
      MapleCoconut event = map.getCoconut();
      MapleCoconuts nut = event.getCoconut(packet.id());
      if (!nut.isHittable()) {
         return;
      }
      if (event == null) {
         return;
      }
      if (currentServerTime() < nut.getHitTime()) {
         return;
      }
      if (nut.getHits() > 2 && Math.random() < 0.4) {
         if (Math.random() < 0.01 && event.getStopped() > 0) {
            nut.setHittable(false);
            event.stopCoconut();
            MasterBroadcaster.getInstance().sendToAllInMap(map, new CoconutHit(false, packet.id(), 1));
            return;
         }
         nut.setHittable(false); // for sure :)
         nut.resetHits(); // For next event (without restarts)
         if (Math.random() < 0.05 && event.getBombings() > 0) {
            MasterBroadcaster.getInstance().sendToAllInMap(map, new CoconutHit(false, packet.id(), 2));
            event.bombCoconut();
         } else if (event.getFalling() > 0) {
            MasterBroadcaster.getInstance().sendToAllInMap(map, new CoconutHit(false, packet.id(), 3));
            event.fallCoconut();
            if (client.getPlayer().getTeam() == 0) {
               event.addMapleScore();
               MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.PINK_TEXT, client.getPlayer().getName() + " of Team Maple knocks down a coconut.");
            } else {
               event.addStoryScore();
               MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.PINK_TEXT, client.getPlayer().getName() + " of Team Story knocks down a coconut.");
            }
            MasterBroadcaster.getInstance().sendToAllInMap(map, new CoconutScore(event.getMapleScore(), event.getStoryScore()));
         }
      } else {
         nut.hit();
         MasterBroadcaster.getInstance().sendToAllInMap(map, new CoconutHit(false, packet.id(), 1));
      }
   }
}  
