package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.SelectWorld;

public class SelectWorldPacketFactory extends AbstractPacketFactory {
   private static SelectWorldPacketFactory instance;

   public static SelectWorldPacketFactory getInstance() {
      if (instance == null) {
         instance = new SelectWorldPacketFactory();
      }
      return instance;
   }

   private SelectWorldPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof SelectWorld) {
         return create(this::selectWorld, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] selectWorld(SelectWorld packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.LAST_CONNECTED_WORLD.getValue());
      mplew.writeInt(packet.worldId());//According to GMS, it should be the world that contains the most characters (most active)
      return mplew.getPacket();
   }
}