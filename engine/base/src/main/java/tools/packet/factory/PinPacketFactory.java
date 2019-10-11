package tools.packet.factory;

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
      Handler.handle(PinCodePacket.class).decorate(this::pinOperation).size(3).register(registry);
      Handler.handle(PinRegistered.class).decorate(this::pinRegistered).size(3).register(registry);
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