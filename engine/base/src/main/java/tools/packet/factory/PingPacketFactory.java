package tools.packet.factory;

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
      Handler.handle(Ping.class).decorate(this::getPing).size(2).register(registry);
   }

   /**
    * Sends a ping packet.
    *
    * @return The packet.
    */
   protected void getPing(MaplePacketLittleEndianWriter writer, Ping packet) {
   }
}