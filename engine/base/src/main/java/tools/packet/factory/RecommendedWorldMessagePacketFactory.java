package tools.packet.factory;

import net.opcodes.SendOpcode;
import server.WorldRecommendation;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.RecommendedWorldMessage;

public class RecommendedWorldMessagePacketFactory extends AbstractPacketFactory {
   private static RecommendedWorldMessagePacketFactory instance;

   public static RecommendedWorldMessagePacketFactory getInstance() {
      if (instance == null) {
         instance = new RecommendedWorldMessagePacketFactory();
      }
      return instance;
   }

   private RecommendedWorldMessagePacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof RecommendedWorldMessage) {
         return create(this::sendRecommended, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] sendRecommended(RecommendedWorldMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.RECOMMENDED_WORLD_MESSAGE.getValue());
      mplew.write(packet.worlds().size());//size
      for (WorldRecommendation world : packet.worlds()) {
         mplew.writeInt(world.worldId());
         mplew.writeMapleAsciiString(world.reason());
      }
      return mplew.getPacket();
   }
}