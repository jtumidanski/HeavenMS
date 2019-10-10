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
      registry.setHandler(RecommendedWorldMessage.class, packet -> create(SendOpcode.RECOMMENDED_WORLD_MESSAGE, this::sendRecommended, packet));
   }

   protected void sendRecommended(MaplePacketLittleEndianWriter writer, RecommendedWorldMessage packet) {
      writer.write(packet.worlds().size());//size
      for (WorldRecommendation world : packet.worlds()) {
         writer.writeInt(world.worldId());
         writer.writeMapleAsciiString(world.reason());
      }
   }
}