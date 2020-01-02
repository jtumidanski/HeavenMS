package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ReactorHitPacket;
import net.server.channel.packet.reader.ReactorHitReader;
import server.maps.MapleReactor;

public final class ReactorHitHandler extends AbstractPacketHandler<ReactorHitPacket> {
   @Override
   public Class<ReactorHitReader> getReaderClass() {
      return ReactorHitReader.class;
   }

   @Override
   public void handlePacket(ReactorHitPacket packet, MapleClient client) {
      MapleReactor reactor = client.getPlayer().getMap().getReactorByOid(packet.objectId());
      if (reactor != null) {
         reactor.hitReactor(true, packet.characterPosition(), packet.stance(), packet.skillId(), client);
      }
   }
}
