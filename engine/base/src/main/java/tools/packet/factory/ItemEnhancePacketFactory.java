package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof SendVegaScroll) {
         return create(this::sendVegaScroll, packetInput);
      } else if (packetInput instanceof SendHammer) {
         return create(this::sendHammerData, packetInput);
      } else if (packetInput instanceof SendHammerMessage) {
         return create(this::sendHammerMessage, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
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