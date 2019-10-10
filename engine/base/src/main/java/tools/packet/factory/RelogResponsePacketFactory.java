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
      registry.setHandler(RelogResponse.class, packet -> this.getRelogResponse((RelogResponse) packet));
   }

   /**
    * Gets the response to a relog request.
    *
    * @return The relog response packet.
    */
   protected byte[] getRelogResponse(RelogResponse packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.RELOG_RESPONSE.getValue());
      mplew.write(1);//1 O.O Must be more types ):
      return mplew.getPacket();
   }
}