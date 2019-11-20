package tools.packet.factory;

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
      Handler.handle(RecommendedWorldMessage.class).decorate(this::sendRecommended).register(registry);
   }

   protected void sendRecommended(MaplePacketLittleEndianWriter writer, RecommendedWorldMessage packet) {
      writer.write(packet.worlds().size());//size
      for (WorldRecommendation world : packet.worlds()) {
         writer.writeInt(world.worldId());
         writer.writeMapleAsciiString(world.reason());
      }
   }
}