package net.server.handlers.login;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.mina.core.session.IoSession;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.reader.ViewAllCharactersSelectedWithPicReader;
import net.server.coordinator.session.MapleSessionCoordinator;
import net.server.coordinator.session.MapleSessionCoordinator.AntiMultiClientResult;
import net.server.login.packet.ViewAllCharactersSelectedWithPicPacket;
import net.server.world.World;
import tools.PacketCreator;
import tools.Randomizer;
import tools.packet.AfterLoginError;
import tools.packet.serverlist.ServerIP;
import tools.packet.WrongPic;

public class ViewAllCharSelectedWithPicHandler extends AbstractPacketHandler<ViewAllCharactersSelectedWithPicPacket> {
   @Override
   public Class<ViewAllCharactersSelectedWithPicReader> getReaderClass() {
      return ViewAllCharactersSelectedWithPicReader.class;
   }

   @Override
   public void handlePacket(ViewAllCharactersSelectedWithPicPacket packet, MapleClient client) {
      if (!packet.hwid().matches("[0-9A-F]{12}_[0-9A-F]{8}")) {
         PacketCreator.announce(client, new AfterLoginError(17));
         return;
      }

      client.updateMacs(packet.macs());
      client.updateHWID(packet.hwid());

      if (client.hasBannedMac() || client.hasBannedHWID()) {
         MapleSessionCoordinator.getInstance().closeSession(client.getSession(), true);
         return;
      }

      IoSession session = client.getSession();

      Server server = Server.getInstance();
      if (!server.haveCharacterEntry(client.getAccID(), packet.characterId())) {
         MapleSessionCoordinator.getInstance().closeSession(client.getSession(), true);
         return;
      }

      client.setWorld(server.getCharacterWorld(packet.characterId()));
      World world = client.getWorldServer();
      if (world == null || world.isWorldCapacityFull()) {
         PacketCreator.announce(client, new AfterLoginError(10));
         return;
      }

      int channel = Randomizer.rand(1, world.getChannelsSize());
      client.setChannel(channel);

      if (client.checkPic(packet.pic())) {
         String[] socket = server.getInetSocket(client.getWorld(), client.getChannel());
         if (socket == null) {
            PacketCreator.announce(client, new AfterLoginError(10));
            return;
         }

         AntiMultiClientResult res = MapleSessionCoordinator.getInstance().attemptGameSession(session, client.getAccID(), packet.hwid());
         if (res != AntiMultiClientResult.SUCCESS) {
            PacketCreator.announce(client, new AfterLoginError(parseAntiMultiClientError(res)));
            return;
         }

         server.unregisterLoginState(client);
         client.setCharacterOnSessionTransitionState(packet.characterId());

         try {
            PacketCreator.announce(client, new ServerIP(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), packet.characterId()));
         } catch (UnknownHostException e) {
            e.printStackTrace();
         }
      } else {
         PacketCreator.announce(client, new WrongPic());
      }
   }

   private int parseAntiMultiClientError(AntiMultiClientResult res) {
      return switch (res) {
         case REMOTE_PROCESSING -> 10;
         case REMOTE_LOGGEDIN -> 7;
         case REMOTE_NO_MATCH -> 17;
         case COORDINATOR_ERROR -> 8;
         default -> 9;
      };
   }
}
