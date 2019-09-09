package net.server.handlers.login;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.login.packet.AcceptToSPacket;
import net.server.channel.packet.reader.AcceptToSReader;
import tools.MaplePacketCreator;

/**
 * @author kevintjuh93
 */
public final class AcceptToSHandler extends AbstractPacketHandler<AcceptToSPacket, AcceptToSReader> {
   @Override
   public Class<AcceptToSReader> getReaderClass() {
      return AcceptToSReader.class;
   }

   @Override
   public void handlePacket(AcceptToSPacket packet, MapleClient client) {
      if (packet.bytes().length == 0 || client.acceptToS()) {
         //Client dc's but just because I am cool I do this (:
         client.disconnect(false, false);
         return;
      }
      if (client.finishLogin() == 0) {
         client.announce(MaplePacketCreator.getAuthSuccess(client));
      } else {
         client.announce(MaplePacketCreator.getLoginFailed(9));//shouldn't happen XD
      }
   }

   @Override
   public boolean validateState(MapleClient c) {
      return !c.isLoggedIn();
   }
}
