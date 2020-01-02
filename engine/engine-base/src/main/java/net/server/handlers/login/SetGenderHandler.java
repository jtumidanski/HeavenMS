package net.server.handlers.login;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.reader.SetGenderReader;
import net.server.coordinator.session.MapleSessionCoordinator;
import net.server.login.packet.SetGenderPacket;
import tools.PacketCreator;
import tools.packet.login.AuthSuccess;

public class SetGenderHandler extends AbstractPacketHandler<SetGenderPacket> {
   @Override
   public Class<SetGenderReader> getReaderClass() {
      return SetGenderReader.class;
   }

   @Override
   public void handlePacket(SetGenderPacket packet, MapleClient client) {
      if (packet.confirmed() == 0x01) {
         client.setGender(packet.gender());
         PacketCreator.announce(client, new AuthSuccess(client));
         Server.getInstance().registerLoginState(client);
      } else {
         MapleSessionCoordinator.getInstance().closeSession(client.getSession(), null);
         client.updateLoginState(MapleClient.LOGIN_NOT_LOGGED_IN);
      }
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return client.getGender() == 10;
   }
}
