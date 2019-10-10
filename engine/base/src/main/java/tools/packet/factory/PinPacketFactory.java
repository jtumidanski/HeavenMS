package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      registry.setHandler(PinCodePacket.class, packet -> this.pinOperation((PinCodePacket) packet));
      registry.setHandler(PinRegistered.class, packet -> this.pinRegistered((PinRegistered) packet));
   }

   /**
    * Gets a packet detailing a PIN operation.
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