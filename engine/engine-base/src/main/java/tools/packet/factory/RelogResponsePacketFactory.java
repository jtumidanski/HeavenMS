package tools.packet.factory;

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
      Handler.handle(RelogResponse.class).decorate(this::getRelogResponse).size(3).register(registry);
   }

   /**
    * Gets the response to a relog request.
    */
   protected void getRelogResponse(MaplePacketLittleEndianWriter writer, RelogResponse packet) {
      writer.write(1);//1 O.O Must be more types ):
   }
}