package net.server.handlers.login;

import client.MapleClient;
import net.server.login.packet.LoginPasswordPacket;
import tools.PacketCreator;
import tools.packet.GuestTOS;

public final class GuestLoginHandler extends LoginPasswordHandler {
   @Override
   public void handlePacket(LoginPasswordPacket packet, MapleClient client) {
      PacketCreator.announce(client, new GuestTOS());
      super.handlePacket(packet, client);
   }
}
