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
import net.MaplePacketHandler;
import net.server.Server;
import net.server.coordinator.MapleSessionCoordinator;
import tools.BCrypt;
import tools.DatabaseConnection;
import tools.HexTool;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public final class LoginPasswordHandler implements MaplePacketHandler {

   private static String hashpwSHA512(String pwd) throws NoSuchAlgorithmException, UnsupportedEncodingException {
      MessageDigest digester = MessageDigest.getInstance("SHA-512");
      digester.update(pwd.getBytes("UTF-8"), 0, pwd.length());
      return HexTool.toString(digester.digest()).replace(" ", "").toLowerCase();
   }

   private static String getRemoteIp(IoSession session) {
      return MapleSessionCoordinator.getSessionRemoteAddress(session);
   }

   private static void login(MapleClient c) {
      c.announce(MaplePacketCreator.getAuthSuccess(c));//why the fk did I do c.getAccountName()?
      Server.getInstance().registerLoginState(c);
   }

   @Override
   public boolean validateState(MapleClient c) {
      return !c.isLoggedIn();
   }

   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      String remoteHost = getRemoteIp(c.getSession());
      if (!remoteHost.contentEquals("null")) {
         if (ServerConstants.USE_IP_VALIDATION) {    // thanks Alex (CanIGetaPR) for suggesting IP validation as a server flag
            if (remoteHost.startsWith("127.")) {
               if (!ServerConstants.LOCALSERVER) { // thanks Mills for noting HOST can also have a field named "localhost"
                  c.announce(MaplePacketCreator.getLoginFailed(13));  // cannot login as localhost if it's not a local server
                  return;
               }
            } else {
               if (ServerConstants.LOCALSERVER) {
                  c.announce(MaplePacketCreator.getLoginFailed(13));  // cannot login as non-localhost if it's a local server
                  return;
               }
            }
         }
      } else {
         c.announce(MaplePacketCreator.getLoginFailed(14));          // thanks Alchemist for noting remoteHost could be null
         return;
      }

      String login = slea.readMapleAsciiString();
      String pwd = slea.readMapleAsciiString();
      c.setAccountName(login);

      slea.skip(6);   // localhost masked the initial part with zeroes...
      byte[] hwidNibbles = slea.read(4);
      int loginStatus = c.login(login, pwd, HexTool.toCompressedString(hwidNibbles));

      if (ServerConstants.AUTOMATIC_REGISTER && loginStatus == 5) {
         try {
            String password = ServerConstants.BCRYPT_MIGRATION ? BCrypt.hashpw(pwd, BCrypt.gensalt(12)) : hashpwSHA512(pwd);
            DatabaseConnection.getInstance().withConnection(connection -> c.setAccID(AccountAdministrator.getInstance().create(connection, login, password)));
         } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            c.setAccID(-1);
            e.printStackTrace();
         }

         loginStatus = c.login(login, pwd, HexTool.toCompressedString(hwidNibbles));
      }

      if (ServerConstants.BCRYPT_MIGRATION && (loginStatus <= -10)) { // -10 means migration to bcrypt, -23 means TOS wasn't accepted
         String password = BCrypt.hashpw(pwd, BCrypt.gensalt(12));
         DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().updatePasswordByName(connection, login, password));
         loginStatus = (loginStatus == -10) ? 0 : 23;
      }

      if (c.hasBannedIP() || c.hasBannedMac()) {
         c.announce(MaplePacketCreator.getLoginFailed(3));
         return;
      }
      Calendar tempban = c.getTempBanCalendarFromDB();
      if (tempban != null) {
         if (tempban.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
            c.announce(MaplePacketCreator.getTempBan(tempban.getTimeInMillis(), c.getGReason()));
            return;
         }
      }
      if (loginStatus == 3) {
         c.announce(MaplePacketCreator.getPermBan(c.getGReason()));//crashes but idc :D
         return;
      } else if (loginStatus != 0) {
         c.announce(MaplePacketCreator.getLoginFailed(loginStatus));
         return;
      }
      if (c.finishLogin() == 0) {
         login(c);
      } else {
         c.announce(MaplePacketCreator.getLoginFailed(7));
      }
   }
}
