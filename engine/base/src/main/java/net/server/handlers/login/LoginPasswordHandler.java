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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.apache.mina.core.session.IoSession;

import client.MapleClient;
import client.database.administrator.AccountAdministrator;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.reader.LoginPasswordReader;
import net.server.coordinator.MapleSessionCoordinator;
import net.server.login.packet.LoginPasswordPacket;
import tools.BCrypt;
import tools.DatabaseConnection;
import tools.HexTool;
import tools.MaplePacketCreator;

public class LoginPasswordHandler extends AbstractPacketHandler<LoginPasswordPacket> {
   @Override
   public Class<LoginPasswordReader> getReaderClass() {
      return LoginPasswordReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      String remoteHost = getRemoteIp(client.getSession());
      if (!remoteHost.contentEquals("null")) {
         if (ServerConstants.USE_IP_VALIDATION) {    // thanks Alex (CanIGetaPR) for suggesting IP validation as a server flag
            if (remoteHost.startsWith("127.")) {
               if (!ServerConstants.LOCALSERVER) { // thanks Mills for noting HOST can also have a field named "localhost"
                  client.announce(MaplePacketCreator.getLoginFailed(13));  // cannot login as localhost if it's not a local server
                  return false;
               }
            } else {
               if (ServerConstants.LOCALSERVER) {
                  client.announce(MaplePacketCreator.getLoginFailed(13));  // cannot login as non-localhost if it's a local server
                  return false;
               }
            }
         }
      } else {
         client.announce(MaplePacketCreator.getLoginFailed(14));          // thanks Alchemist for noting remoteHost could be null
         return false;
      }
      return true;
   }

   @Override
   public void handlePacket(LoginPasswordPacket packet, MapleClient client) {
      client.setAccountName(packet.login());

      int loginStatus = client.login(packet.login(), packet.password(), HexTool.toCompressedString(packet.hwid()));

      if (ServerConstants.AUTOMATIC_REGISTER && loginStatus == 5) {
         try {
            String password = ServerConstants.BCRYPT_MIGRATION ? BCrypt.hashpw(packet.password(), BCrypt.gensalt(12)) : hashpwSHA512(packet.password());
            DatabaseConnection.getInstance().withConnection(connection -> client.setAccID(AccountAdministrator.getInstance().create(connection, packet.login(), password)));
         } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            client.setAccID(-1);
            e.printStackTrace();
         }

         loginStatus = client.login(packet.login(), packet.password(), HexTool.toCompressedString(packet.hwid()));
      }

      if (ServerConstants.BCRYPT_MIGRATION && (loginStatus <= -10)) { // -10 means migration to bcrypt, -23 means TOS wasn't accepted
         String password = BCrypt.hashpw(packet.password(), BCrypt.gensalt(12));
         DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().updatePasswordByName(connection, packet.login(), password));
         loginStatus = (loginStatus == -10) ? 0 : 23;
      }

      if (client.hasBannedIP() || client.hasBannedMac()) {
         client.announce(MaplePacketCreator.getLoginFailed(3));
         return;
      }
      Calendar tempban = client.getTempBanCalendarFromDB();
      if (tempban != null) {
         if (tempban.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
            client.announce(MaplePacketCreator.getTempBan(tempban.getTimeInMillis(), client.getGReason()));
            return;
         }
      }
      if (loginStatus == 3) {
         client.announce(MaplePacketCreator.getPermBan(client.getGReason()));//crashes but idc :D
         return;
      } else if (loginStatus != 0) {
         client.announce(MaplePacketCreator.getLoginFailed(loginStatus));
         return;
      }
      if (client.finishLogin() == 0) {
         login(client);
      } else {
         client.announce(MaplePacketCreator.getLoginFailed(7));
      }
   }

   private String hashpwSHA512(String pwd) throws NoSuchAlgorithmException, UnsupportedEncodingException {
      MessageDigest digester = MessageDigest.getInstance("SHA-512");
      digester.update(pwd.getBytes("UTF-8"), 0, pwd.length());
      return HexTool.toString(digester.digest()).replace(" ", "").toLowerCase();
   }

   private String getRemoteIp(IoSession session) {
      return MapleSessionCoordinator.getSessionRemoteAddress(session);
   }

   private void login(MapleClient client) {
      client.announce(MaplePacketCreator.getAuthSuccess(client));//why the fk did I do client.getAccountName()?
      Server.getInstance().registerLoginState(client);
   }

   @Override
   public boolean validateState(MapleClient client) {
      return !client.isLoggedIn();
   }
}
