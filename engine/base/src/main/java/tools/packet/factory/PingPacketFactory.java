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
      registry.setHandler(Ping.class, packet -> create(SendOpcode.PING, this::getPing, packet, 2));
   }

   /**
    * Sends a ping packet.
    *
    * @return The packet.
    */
   protected void getPing(MaplePacketLittleEndianWriter writer, Ping packet) {
   }
}