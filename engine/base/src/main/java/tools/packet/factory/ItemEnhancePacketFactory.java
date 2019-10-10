package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.item.enhance.SendHammer;
import tools.packet.item.enhance.SendHammerMessage;
import tools.packet.item.enhance.SendVegaScroll;

public class ItemEnhancePacketFactory extends AbstractPacketFactory {
   private static ItemEnhancePacketFactory instance;

   public static ItemEnhancePacketFactory getInstance() {
      if (instance == null) {
         instance = new ItemEnhancePacketFactory();
      }
      return instance;
   }

   private ItemEnhancePacketFactory() {
      registry.setHandler(SendVegaScroll.class, packet -> create(SendOpcode.VEGA_SCROLL, this::sendVegaScroll, packet, 3));
      registry.setHandler(SendHammer.class, packet -> create(SendOpcode.VICIOUS_HAMMER, this::sendHammerData, packet));
      registry.setHandler(SendHammerMessage.class, packet -> create(SendOpcode.VICIOUS_HAMMER, this::sendHammerMessage, packet));
   }

   protected void sendVegaScroll(MaplePacketLittleEndianWriter writer, SendVegaScroll packet) {
      writer.write(packet.operation().getValue());
   }

   protected void sendHammerData(MaplePacketLittleEndianWriter writer, SendHammer packet) {
      writer.write(0x39);
      writer.writeInt(0);
      writer.writeInt(packet.used());
   }

   protected void sendHammerMessage(MaplePacketLittleEndianWriter writer, SendHammerMessage packet) {
      writer.write(0x3D);
      writer.writeInt(0);
   }
}