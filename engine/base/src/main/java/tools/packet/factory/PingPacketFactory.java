package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.Ping;

public class PingPacketFactory extends AbstractPacketFactory {
   private static PingPacketFactory instance;

   public static PingPacketFactory getInstance() {
      if (instance == null) {
         instance = new PingPacketFactory();
      }
      return instance;
   }

   private PingPacketFactory() {
      registry.setHandler(Ping.class, packet -> this.getPing((Ping) packet));
   }

   /**
    * Sends a ping packet.
    *
    * @return The packet.
    */
   protected byte[] getPing(Ping packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
      mplew.writeShort(SendOpcode.PING.getValue());
      return mplew.getPacket();
   }
}