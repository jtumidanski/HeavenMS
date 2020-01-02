package net.server.handlers.login;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.RegisterPinReader;
import net.server.coordinator.session.MapleSessionCoordinator;
import net.server.login.packet.RegisterPinPacket;
import tools.PacketCreator;
import tools.packet.pin.PinRegistered;

public final class RegisterPinHandler extends AbstractPacketHandler<RegisterPinPacket> {
   @Override
   public Class<RegisterPinReader> getReaderClass() {
      return RegisterPinReader.class;
   }

   @Override
   public void handlePacket(RegisterPinPacket packet, MapleClient client) {
      if (packet.byte1() == 0) {
         MapleSessionCoordinator.getInstance().closeSession(client.getSession(), null);
         client.updateLoginState(MapleClient.LOGIN_NOT_LOGGED_IN);
      } else {
         if (packet.pin() != null) {
            client.setPin(packet.pin());
            PacketCreator.announce(client, new PinRegistered());

            MapleSessionCoordinator.getInstance().closeSession(client.getSession(), null);
            client.updateLoginState(MapleClient.LOGIN_NOT_LOGGED_IN);
         }
      }
   }
}
