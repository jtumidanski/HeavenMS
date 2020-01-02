package net.server.handlers.login;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.AcceptToSReader;
import net.server.login.packet.AcceptToSPacket;
import tools.PacketCreator;
import tools.packet.login.LoginFailedReason;
import tools.packet.login.AuthSuccess;
import tools.packet.login.LoginFailed;

public final class AcceptToSHandler extends AbstractPacketHandler<AcceptToSPacket> {
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
         PacketCreator.announce(client, new AuthSuccess(client));
      } else {
         PacketCreator.announce(client, new LoginFailed(LoginFailedReason.SYSTEM_ERROR_3));
      }
   }

   @Override
   public boolean validateState(MapleClient client) {
      return !client.isLoggedIn();
   }
}
