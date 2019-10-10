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
      registry.setHandler(PinCodePacket.class, packet -> create(SendOpcode.CHECK_PINCODE, this::pinOperation, packet, 3));
      registry.setHandler(PinRegistered.class, packet -> create(SendOpcode.UPDATE_PINCODE, this::pinRegistered, packet, 3));
   }

   /**
    * Gets a packet detailing a PIN operation.
    */
   protected void pinOperation(MaplePacketLittleEndianWriter writer, PinCodePacket packet) {
      writer.write(packet.operation().getValue());
   }

   protected void pinRegistered(MaplePacketLittleEndianWriter writer, PinRegistered packet) {
      writer.write(0);
   }
}