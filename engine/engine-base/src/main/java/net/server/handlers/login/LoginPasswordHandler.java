package net.server.handlers.login;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.apache.mina.core.session.IoSession;

import client.MapleClient;
import database.DatabaseConnection;
import database.administrator.AccountAdministrator;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.reader.LoginPasswordReader;
import net.server.coordinator.session.MapleSessionCoordinator;
import net.server.login.packet.LoginPasswordPacket;
import tools.BCrypt;
import tools.HexTool;
import tools.PacketCreator;
import tools.packet.login.AuthSuccess;
import tools.packet.login.LoginFailed;
import tools.packet.login.LoginFailedReason;
import tools.packet.login.PermanentBan;
import tools.packet.login.TemporaryBan;

public class LoginPasswordHandler extends AbstractPacketHandler<LoginPasswordPacket> {
   @Override
   public Class<LoginPasswordReader> getReaderClass() {
      return LoginPasswordReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      String remoteHost = getRemoteIp(client.getSession());
      if (!remoteHost.contentEquals("null")) {
         if (YamlConfig.config.server.USE_IP_VALIDATION) {
            if (remoteHost.startsWith("127.")) {
               if (!YamlConfig.config.server.LOCALSERVER) {
                  // cannot login as localhost if it's not a local server
                  PacketCreator.announce(client, new LoginFailed(LoginFailedReason.UNABLE_TO_LOG_ON_AS_MASTER_AT_IP));
                  return false;
               }
            } else {
               if (YamlConfig.config.server.LOCALSERVER) {
                  // cannot login as non-localhost if it's a local server
                  PacketCreator.announce(client, new LoginFailed(LoginFailedReason.UNABLE_TO_LOG_ON_AS_MASTER_AT_IP));
                  return false;
               }
            }
         }
      } else {
         PacketCreator.announce(client, new LoginFailed(LoginFailedReason.WRONG_GATEWAY));
         return false;
      }
      return true;
   }

   @Override
   public void handlePacket(LoginPasswordPacket packet, MapleClient client) {
      client.setAccountName(packet.login());

      String nibbleHwid = HexTool.toCompressedString(packet.hwid());
      int loginStatus = client.login(packet.login(), packet.password(), nibbleHwid);

      if (YamlConfig.config.server.AUTOMATIC_REGISTER && loginStatus == 5) {
         try {
            String password = YamlConfig.config.server.BCRYPT_MIGRATION ? BCrypt.hashpw(packet.password(), BCrypt.gensalt(12)) : hashPasswordSHA512(packet.password());
            DatabaseConnection.getInstance().withConnection(connection -> client.setAccID(AccountAdministrator.getInstance().create(connection, packet.login(), password)));
         } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            client.setAccID(-1);
            e.printStackTrace();
         }

         loginStatus = client.login(packet.login(), packet.password(), nibbleHwid);
      }

      if (YamlConfig.config.server.BCRYPT_MIGRATION && (loginStatus <= -10)) { // -10 means migration to bcrypt, -23 means TOS wasn't accepted
         String password = BCrypt.hashpw(packet.password(), BCrypt.gensalt(12));
         DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().updatePasswordByName(connection, packet.login(), password));
         loginStatus = (loginStatus == -10) ? 0 : 23;
      }

      if (client.hasBannedIP() || client.hasBannedMac()) {
         PacketCreator.announce(client, new LoginFailed(LoginFailedReason.DELETED_OR_BLOCKED));
         return;
      }
      Calendar tempBanCalendar = client.getTempBanCalendarFromDB();
      if (tempBanCalendar != null) {
         if (tempBanCalendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
            PacketCreator.announce(client, new TemporaryBan(tempBanCalendar.getTimeInMillis(), client.getGReason()));
            return;
         }
      }
      if (loginStatus == 3) {
         PacketCreator.announce(client, new PermanentBan(client.getGReason()));
         return;
      } else if (loginStatus != 0) {
         LoginFailedReason reason = LoginFailedReason.fromValue(loginStatus);
         PacketCreator.announce(client, new LoginFailed(reason));
         return;
      }
      if (client.finishLogin() == 0) {
         client.checkChar(client.getAccID());
         login(client);
      } else {
         PacketCreator.announce(client, new LoginFailed(LoginFailedReason.ALREADY_LOGGED_IN));
      }
   }

   private String hashPasswordSHA512(String pwd) throws NoSuchAlgorithmException, UnsupportedEncodingException {
      MessageDigest digester = MessageDigest.getInstance("SHA-512");
      digester.update(pwd.getBytes("UTF-8"), 0, pwd.length());
      return HexTool.toString(digester.digest()).replace(" ", "").toLowerCase();
   }

   private String getRemoteIp(IoSession session) {
      return MapleSessionCoordinator.getSessionRemoteAddress(session);
   }

   private void login(MapleClient client) {
      PacketCreator.announce(client, new AuthSuccess(client));
      Server.getInstance().registerLoginState(client);
   }

   @Override
   public boolean validateState(MapleClient client) {
      return !client.isLoggedIn();
   }
}
