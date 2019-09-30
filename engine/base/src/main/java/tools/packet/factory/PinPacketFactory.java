package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.pin.PinCodePacket;
import tools.packet.pin.PinRegistered;

public class PinPacketFactory extends AbstractPacketFactory {
   private static PinPacketFactory instance;

   public static PinPacketFactory getInstance() {
      if (instance == null) {
         instance = new PinPacketFactory();
      }
      return instance;
   }

   private PinPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof PinCodePacket) {
         return create(this::pinOperation, packetInput);
      } else if (packetInput instanceof PinRegistered) {
         return create(this::pinRegistered, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Gets a packet detailing a PIN operation.
    *
    * @return
    */
   protected byte[] pinOperation(PinCodePacket packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.CHECK_PINCODE.getValue());
      mplew.write(packet.operation().getValue());
      return mplew.getPacket();
   }

   protected byte[] pinRegistered(PinRegistered packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.UPDATE_PINCODE.getValue());
      mplew.write(0);
      return mplew.getPacket();
   }
}