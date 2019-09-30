package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.Randomizer;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.GuestTOS;
import tools.packet.PacketInput;

public class GuestLoginPacketFactory extends AbstractPacketFactory {
   private static GuestLoginPacketFactory instance;

   public static GuestLoginPacketFactory getInstance() {
      if (instance == null) {
         instance = new GuestLoginPacketFactory();
      }
      return instance;
   }

   private GuestLoginPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof GuestTOS) {
         return create(this::sendGuestTOS, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
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