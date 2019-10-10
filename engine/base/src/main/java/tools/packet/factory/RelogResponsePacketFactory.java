package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.RelogResponse;

public class RelogResponsePacketFactory extends AbstractPacketFactory {
   private static RelogResponsePacketFactory instance;

   public static RelogResponsePacketFactory getInstance() {
      if (instance == null) {
         instance = new RelogResponsePacketFactory();
      }
      return instance;
   }

   private RelogResponsePacketFactory() {
      registry.setHandler(RelogResponse.class, packet -> create(SendOpcode.RELOG_RESPONSE, this::getRelogResponse, packet, 3));
   }

   /**
    * Gets the response to a relog request.
    *
    * @return The relog response packet.
    */
   protected void getRelogResponse(MaplePacketLittleEndianWriter writer, RelogResponse packet) {
      writer.write(1);//1 O.O Must be more types ):
   }
}