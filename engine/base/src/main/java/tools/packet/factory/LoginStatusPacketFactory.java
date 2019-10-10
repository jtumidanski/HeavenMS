package tools.packet.factory;

import client.MapleClient;
import constants.ServerConstants;
import net.opcodes.SendOpcode;
import net.server.Server;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.login.AuthSuccess;
import tools.packet.login.LoginFailed;
import tools.packet.login.PermanentBan;
import tools.packet.login.TemporaryBan;

public class LoginStatusPacketFactory extends AbstractPacketFactory {
   private static LoginStatusPacketFactory instance;

   public static LoginStatusPacketFactory getInstance() {
      if (instance == null) {
         instance = new LoginStatusPacketFactory();
      }
      return instance;
   }

   private LoginStatusPacketFactory() {
      registry.setHandler(LoginFailed.class, packet -> create(SendOpcode.LOGIN_STATUS, this::getLoginFailed, packet, 8));
      registry.setHandler(PermanentBan.class, packet -> create(SendOpcode.LOGIN_STATUS, this::getPermBan, packet));
      registry.setHandler(TemporaryBan.class, packet -> create(SendOpcode.LOGIN_STATUS, this::getTempBan, packet, 17));
      registry.setHandler(AuthSuccess.class, packet -> create(SendOpcode.LOGIN_STATUS, this::getAuthSuccess, packet));
   }

   /**
    * Gets a login failed packet.
    *
    * @return The login failed packet.
    */
   protected void getLoginFailed(MaplePacketLittleEndianWriter writer, LoginFailed packet) {
      writer.write(packet.reason().getValue());
      writer.write(0);
      writer.writeInt(0);
   }

   protected void getPermBan(MaplePacketLittleEndianWriter writer, PermanentBan packet) {
      writer.write(2); // Account is banned
      writer.write(0);
      writer.writeInt(0);
      writer.write(0);
      writer.writeLong(getTime(-1));
   }

   protected void getTempBan(MaplePacketLittleEndianWriter writer, TemporaryBan packet) {
      writer.write(2);
      writer.write(0);
      writer.writeInt(0);
      writer.write(packet.reason());
      writer.writeLong(getTime(packet.timestampUntil())); // Tempban date is handled as a 64-bit long, number of 100NS intervals since 1/1/1601. Lulz.
   }

   /**
    * Gets a successful authentication packet.
    *
    * @return the successful authentication packet
    */
   protected void getAuthSuccess(MaplePacketLittleEndianWriter writer, AuthSuccess packet) {
      MapleClient client = packet.getClient();
      Server.getInstance().loadAccountCharacters(client);    // locks the login session until data is recovered from the cache or the DB.
      Server.getInstance().loadAccountStorages(client);
      writer.writeInt(0);
      writer.writeShort(0);
      writer.writeInt(client.getAccID());
      writer.write(client.getGender());
      boolean canFly = Server.getInstance().canFly(client.getAccID());
      writer.writeBool((ServerConstants.USE_ENFORCE_ADMIN_ACCOUNT || canFly) && client.getGMLevel() > 1);    // thanks Steve(kaito1410) for pointing the GM account boolean here
      writer.write(((ServerConstants.USE_ENFORCE_ADMIN_ACCOUNT || canFly) && client.getGMLevel() > 1) ? 0x80 : 0);  // Admin Byte. 0x80,0x40,0x20.. Rubbish.
      writer.write(0); // Country Code.
      writer.writeMapleAsciiString(client.getAccountName());
      writer.write(0);
      writer.write(0); // IsQuietBan
      writer.writeLong(0);//IsQuietBanTimeStamp
      writer.writeLong(0); //CreationTimeStamp
      writer.writeInt(1); // 1: Remove the "Select the world you want to play in"
      writer.write(ServerConstants.ENABLE_PIN && client.cannotBypassPin() ? 0 : 1); // 0 = Pin-System Enabled, 1 = Disabled
      writer.write(ServerConstants.ENABLE_PIC && client.cannotBypassPic() ? (client.getPic() == null || client.getPic().equals("") ? 0 : 1) : 2); // 0 = Register PIC, 1 = Ask for PIC, 2 = Disabled
   }
}
