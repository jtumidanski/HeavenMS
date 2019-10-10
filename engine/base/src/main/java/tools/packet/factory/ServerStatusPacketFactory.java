package tools.packet.factory;

import net.opcodes.SendOpcode;
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
      registry.setHandler(GetServerStatus.class, packet -> create(SendOpcode.SERVERSTATUS, this::getServerStatus, packet, 4));
   }

   /**
    * Gets a packet detailing a server status message.
    * <p>
    * Possible values for <code>status</code>:<br> 0 - Normal<br> 1 - Highly
    * populated<br> 2 - Full
    *
    * @return The server status packet.
    */
   protected void getServerStatus(MaplePacketLittleEndianWriter writer, GetServerStatus packet) {
      writer.writeShort(packet.status().getValue());
   }
}