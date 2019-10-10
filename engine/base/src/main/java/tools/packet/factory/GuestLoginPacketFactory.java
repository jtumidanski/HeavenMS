package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.Randomizer;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.GuestTOS;

public class GuestLoginPacketFactory extends AbstractPacketFactory {
   private static GuestLoginPacketFactory instance;

   public static GuestLoginPacketFactory getInstance() {
      if (instance == null) {
         instance = new GuestLoginPacketFactory();
      }
      return instance;
   }

   private GuestLoginPacketFactory() {
      registry.setHandler(GuestTOS.class, packet -> this.sendGuestTOS((GuestTOS) packet));
   }

   protected byte[] sendGuestTOS(GuestTOS packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUEST_ID_LOGIN.getValue());
      mplew.writeShort(0x100);
      mplew.writeInt(Randomizer.nextInt(999999));
      mplew.writeLong(0);
      mplew.writeLong(getTime(-2));
      mplew.writeLong(getTime(System.currentTimeMillis()));
      mplew.writeInt(0);
      mplew.writeMapleAsciiString("http://maplesolaxia.com");
      return mplew.getPacket();
   }
}