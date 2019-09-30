package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.AfterLoginError;
import tools.packet.PacketInput;

public class AfterLoginErrorPacketFactory extends AbstractPacketFactory {
   private static AfterLoginErrorPacketFactory instance;

   public static AfterLoginErrorPacketFactory getInstance() {
      if (instance == null) {
         instance = new AfterLoginErrorPacketFactory();
      }
      return instance;
   }

   private AfterLoginErrorPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof AfterLoginError) {
         return create(this::getAfterLoginError, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] getAfterLoginError(AfterLoginError packet) {//same as above o.o
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(8);
      mplew.writeShort(SendOpcode.SELECT_CHARACTER_BY_VAC.getValue());
      mplew.writeShort(packet.reason());//using other types than stated above = CRASH
      return mplew.getPacket();
   }
}