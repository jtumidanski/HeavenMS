/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.handlers.login;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.mina.core.session.IoSession;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.login.packet.CharacterSelectedPacket;
import net.server.channel.packet.reader.CharacterSelectedReader;
import net.server.coordinator.MapleSessionCoordinator;
import net.server.coordinator.MapleSessionCoordinator.AntiMulticlientResult;
import net.server.world.World;
import tools.MaplePacketCreator;

public final class CharacterSelectedHandler extends AbstractPacketHandler<CharacterSelectedPacket> {
   @Override
   public Class<CharacterSelectedReader> getReaderClass() {
      return CharacterSelectedReader.class;
   }

   @Override
   public void handlePacket(CharacterSelectedPacket packet, MapleClient client) {
      if (!packet.hwid().matches("[0-9A-F]{12}_[0-9A-F]{8}")) {
         client.announce(MaplePacketCreator.getAfterLoginError(17));
         return;
      }

      client.updateMacs(packet.macs());
      client.updateHWID(packet.hwid());

      IoSession session = client.getSession();
      AntiMulticlientResult res = MapleSessionCoordinator.getInstance().attemptGameSession(session, client.getAccID(), packet.hwid());
      if (res != AntiMulticlientResult.SUCCESS) {
         client.announce(MaplePacketCreator.getAfterLoginError(parseAntiMulticlientError(res)));
         return;
      }

      if (client.hasBannedMac() || client.hasBannedHWID()) {
         MapleSessionCoordinator.getInstance().closeSession(session, true);
         return;
      }

      Server server = Server.getInstance();
      if (!server.haveCharacterEntry(client.getAccID(), packet.characterId())) {
         MapleSessionCoordinator.getInstance().closeSession(session, true);
         return;
      }

      client.setWorld(server.getCharacterWorld(packet.characterId()));
      World wserv = client.getWorldServer();
      if (wserv == null || wserv.isWorldCapacityFull()) {
         client.announce(MaplePacketCreator.getAfterLoginError(10));
         return;
      }

      String[] socket = server.getInetSocket(client.getWorld(), client.getChannel());
      if (socket == null) {
         client.announce(MaplePacketCreator.getAfterLoginError(10));
         return;
      }

      server.unregisterLoginState(client);
      client.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
      server.setCharacteridInTransition(session, packet.characterId());

      try {
         client.announce(MaplePacketCreator.getServerIP(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), packet.characterId()));
      } catch (UnknownHostException | NumberFormatException e) {
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