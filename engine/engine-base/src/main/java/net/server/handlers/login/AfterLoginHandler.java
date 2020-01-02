package net.server.handlers.login;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.AfterLoginReader;
import net.server.coordinator.session.MapleSessionCoordinator;
import net.server.login.packet.AfterLoginPacket;
import tools.PacketCreator;
import tools.packet.pin.PinCodePacket;
import tools.packet.pin.PinOperation;

public final class AfterLoginHandler extends AbstractPacketHandler<AfterLoginPacket> {
   @Override
   public Class<AfterLoginReader> getReaderClass() {
      return AfterLoginReader.class;
   }

   @Override
   public void handlePacket(AfterLoginPacket packet, MapleClient client) {
      if (packet.byte1() == 1 && packet.byte2() == 1) {
         if (client.getPin() == null || client.getPin().equals("")) {
            PacketCreator.announce(client, new PinCodePacket(PinOperation.NEW_PIN));
         } else {
            PacketCreator.announce(client, new PinCodePacket(PinOperation.ENTER_PIN));
         }
      } else if (packet.byte1() == 1 && packet.byte2() == 0) {
         if (client.checkPin(packet.pin())) {
            PacketCreator.announce(client, new PinCodePacket(PinOperation.ACCEPTED));
         } else {
            PacketCreator.announce(client, new PinCodePacket(PinOperation.INVALID));
         }
      } else if (packet.byte1() == 2 && packet.byte2() == 0) {
         if (client.checkPin(packet.pin())) {
            PacketCreator.announce(client, new PinCodePacket(PinOperation.NEW_PIN));
         } else {
            PacketCreator.announce(client, new PinCodePacket(PinOperation.INVALID));
         }
      } else if (packet.byte1() == 0 && packet.byte2() == 5) {
         MapleSessionCoordinator.getInstance().closeSession(client.getSession(), null);
         client.updateLoginState(MapleClient.LOGIN_NOT_LOGGED_IN);
      }
   }
}
