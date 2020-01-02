package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.TouchReactorPacket;
import net.server.channel.packet.reader.TouchReactorReader;
import scripting.reactor.ReactorScriptManager;
import server.maps.MapleReactor;

public final class TouchReactorHandler extends AbstractPacketHandler<TouchReactorPacket> {
   @Override
   public Class<TouchReactorReader> getReaderClass() {
      return TouchReactorReader.class;
   }

   @Override
   public void handlePacket(TouchReactorPacket packet, MapleClient client) {
      MapleReactor reactor = client.getPlayer().getMap().getReactorByOid(packet.objectId());
      if (reactor != null) {
         if (packet.isTouching()) {
            ReactorScriptManager.getInstance().touch(client, reactor);
         } else {
            ReactorScriptManager.getInstance().release(client, reactor);
         }
      }
   }
}
