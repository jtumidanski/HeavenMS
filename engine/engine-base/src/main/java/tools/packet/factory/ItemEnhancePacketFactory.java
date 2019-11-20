package tools.packet.factory;

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
      Handler.handle(SendVegaScroll.class).decorate(this::sendVegaScroll).size(3).register(registry);
      Handler.handle(SendHammer.class).decorate(this::sendHammerData).register(registry);
      Handler.handle(SendHammerMessage.class).decorate(this::sendHammerMessage).register(registry);
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