package net.server.handlers.login;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.mina.core.session.IoSession;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.reader.ViewAllCharactersRegisterPicReader;
import net.server.coordinator.MapleSessionCoordinator;
import net.server.coordinator.MapleSessionCoordinator.AntiMulticlientResult;
import net.server.login.packet.ViewAllCharactersRegisterPicPacket;
import net.server.world.World;
import tools.MaplePacketCreator;
import tools.Randomizer;

public final class ViewAllCharRegisterPicHandler extends AbstractPacketHandler<ViewAllCharactersRegisterPicPacket, ViewAllCharactersRegisterPicReader> {
   @Override
   public Class<ViewAllCharactersRegisterPicReader> getReaderClass() {
      return ViewAllCharactersRegisterPicReader.class;
   }

   @Override
   public void handlePacket(ViewAllCharactersRegisterPicPacket packet, MapleClient client) {
      if (!packet.hwid().matches("[0-9A-F]{12}_[0-9A-F]{8}")) {
         client.announce(MaplePacketCreator.getAfterLoginError(17));
         return;
      }

      client.updateMacs(packet.mac());
      client.updateHWID(packet.hwid());

      if (client.hasBannedMac() || client.hasBannedHWID()) {
         MapleSessionCoordinator.getInstance().closeSession(client.getSession(), true);
         return;
      }

      IoSession session = client.getSession();
      AntiMulticlientResult res = MapleSessionCoordinator.getInstance().attemptGameSession(session, client.getAccID(), packet.hwid());
      if (res != AntiMulticlientResult.SUCCESS) {
         client.announce(MaplePacketCreator.getAfterLoginError(parseAntiMulticlientError(res)));
         return;
      }

      Server server = Server.getInstance();
      if (!server.haveCharacterEntry(client.getAccID(), packet.characterId())) {
         MapleSessionCoordinator.getInstance().closeSession(client.getSession(), true);
         return;
      }

      client.setWorld(server.getCharacterWorld(packet.characterId()));
      World wserv = client.getWorldServer();
      if (wserv == null || wserv.isWorldCapacityFull()) {
         client.announce(MaplePacketCreator.getAfterLoginError(10));
         return;
      }

      int channel = Randomizer.rand(1, server.getWorld(client.getWorld()).getChannelsSize());
      client.setChannel(channel);

      client.setPic(packet.pic());

      String[] socket = server.getInetSocket(client.getWorld(), channel);
      if (socket == null) {
         client.announce(MaplePacketCreator.getAfterLoginError(10));
         return;
      }

      server.unregisterLoginState(client);
      client.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
      server.setCharacteridInTransition(session, packet.characterId());

      try {
         client.announce(MaplePacketCreator.getServerIP(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), packet.characterId()));
      } catch (UnknownHostException e) {
         e.printStackTrace();
      }
   }

   private int parseAntiMulticlientError(AntiMulticlientResult res) {
      switch (res) {
         case REMOTE_PROCESSING:
            return 10;

         case REMOTE_LOGGEDIN:
            return 7;

         case REMOTE_NO_MATCH:
            return 17;

         case COORDINATOR_ERROR:
            return 8;

         default:
            return 9;
      }
   }
}
