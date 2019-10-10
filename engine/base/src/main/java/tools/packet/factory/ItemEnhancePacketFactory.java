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
      registry.setHandler(SendVegaScroll.class, packet -> this.sendVegaScroll((SendVegaScroll) packet));
      registry.setHandler(SendHammer.class, packet -> this.sendHammerData((SendHammer) packet));
      registry.setHandler(SendHammerMessage.class, packet -> this.sendHammerMessage((SendHammerMessage) packet));
   }

   protected byte[] sendVegaScroll(SendVegaScroll packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.VEGA_SCROLL.getValue());
      mplew.write(packet.operation().getValue());
      return mplew.getPacket();
   }

   protected byte[] sendHammerData(SendHammer packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.VICIOUS_HAMMER.getValue());
      mplew.write(0x39);
      mplew.writeInt(0);
      mplew.writeInt(packet.used());
      return mplew.getPacket();
   }

   protected byte[] sendHammerMessage(SendHammerMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.VICIOUS_HAMMER.getValue());
      mplew.write(0x3D);
      mplew.writeInt(0);
      return mplew.getPacket();
   }
}