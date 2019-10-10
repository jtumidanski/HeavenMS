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
      registry.setHandler(LoginFailed.class, packet -> this.getLoginFailed((LoginFailed) packet));
      registry.setHandler(PermanentBan.class, packet -> this.getPermBan((PermanentBan) packet));
      registry.setHandler(TemporaryBan.class, packet -> this.getTempBan((TemporaryBan) packet));
      registry.setHandler(AuthSuccess.class, packet -> this.getAuthSuccess((AuthSuccess) packet));
   }

   /**
    * Gets a login failed packet.
    *
    * @return The login failed packet.
    */
   protected byte[] getLoginFailed(LoginFailed packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(8);
      mplew.writeShort(SendOpcode.LOGIN_STATUS.getValue());
      mplew.write(packet.reason().getValue());
      mplew.write(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] getPermBan(PermanentBan packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.LOGIN_STATUS.getValue());
      mplew.write(2); // Account is banned
      mplew.write(0);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeLong(getTime(-1));
      return mplew.getPacket();
   }

   protected byte[] getTempBan(TemporaryBan packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);
      mplew.writeShort(SendOpcode.LOGIN_STATUS.getValue());
      mplew.write(2);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.write(packet.reason());
      mplew.writeLong(getTime(packet.timestampUntil())); // Tempban date is handled as a 64-bit long, number of 100NS intervals since 1/1/1601. Lulz.
      return mplew.getPacket();
   }

   /**
    * Gets a successful authentication packet.
    *
    * @return the successful authentication packet
    */
   protected byte[] getAuthSuccess(AuthSuccess packet) {
      MapleClient client = packet.getClient();
      Server.getInstance().loadAccountCharacters(client);    // locks the login session until data is recovered from the cache or the DB.
      Server.getInstance().loadAccountStorages(client);

      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.LOGIN_STATUS.getValue());
      mplew.writeInt(0);
      mplew.writeShort(0);
      mplew.writeInt(client.getAccID());
      mplew.write(client.getGender());

      boolean canFly = Server.getInstance().canFly(client.getAccID());
      mplew.writeBool((ServerConstants.USE_ENFORCE_ADMIN_ACCOUNT || canFly) && client.getGMLevel() > 1);    // thanks Steve(kaito1410) for pointing the GM account boolean here
      mplew.write(((ServerConstants.USE_ENFORCE_ADMIN_ACCOUNT || canFly) && client.getGMLevel() > 1) ? 0x80 : 0);  // Admin Byte. 0x80,0x40,0x20.. Rubbish.
      mplew.write(0); // Country Code.

      mplew.writeMapleAsciiString(client.getAccountName());
      mplew.write(0);

      mplew.write(0); // IsQuietBan
      mplew.writeLong(0);//IsQuietBanTimeStamp
      mplew.writeLong(0); //CreationTimeStamp

      mplew.writeInt(1); // 1: Remove the "Select the world you want to play in"

      mplew.write(ServerConstants.ENABLE_PIN && client.cannotBypassPin() ? 0 : 1); // 0 = Pin-System Enabled, 1 = Disabled
      mplew.write(ServerConstants.ENABLE_PIC && client.cannotBypassPic() ? (client.getPic() == null || client.getPic().equals("") ? 0 : 1) : 2); // 0 = Register PIC, 1 = Ask for PIC, 2 = Disabled

      return mplew.getPacket();
   }
}
