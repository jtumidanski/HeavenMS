package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.serverlist.GetServerStatus;

public class ServerStatusPacketFactory extends AbstractPacketFactory {
   private static ServerStatusPacketFactory instance;

   public static ServerStatusPacketFactory getInstance() {
      if (instance == null) {
         instance = new ServerStatusPacketFactory();
      }
      return instance;
   }

   private ServerStatusPacketFactory() {
      Handler.handle(GetServerStatus.class).decorate(this::getServerStatus).size(4).register(registry);
   }

   /**
    * Gets a packet detailing a server status message.
    * <p>
    * Possible values for <code>status</code>:<br> 0 - Normal<br> 1 - Highly
    * populated<br> 2 - Full
    */
   protected void getServerStatus(MaplePacketLittleEndianWriter writer, GetServerStatus packet) {
      writer.writeShort(packet.status().getValue());
   }
}