package tools.packet.factory;

import net.opcodes.SendOpcode;
import server.WorldRecommendation;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      registry.setHandler(RecommendedWorldMessage.class, packet -> this.sendRecommended((RecommendedWorldMessage) packet));
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