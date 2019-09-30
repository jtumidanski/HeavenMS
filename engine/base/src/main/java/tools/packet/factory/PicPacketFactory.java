package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.WrongPic;

public class PicPacketFactory extends AbstractPacketFactory {
   private static PicPacketFactory instance;

   public static PicPacketFactory getInstance() {
      if (instance == null) {
         instance = new PicPacketFactory();
      }
      return instance;
   }

   private PicPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof WrongPic) {
         return create(this::wrongPic, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] wrongPic(WrongPic packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.CHECK_SPW_RESULT.getValue());
      mplew.write(0);
      return mplew.getPacket();
   }
}