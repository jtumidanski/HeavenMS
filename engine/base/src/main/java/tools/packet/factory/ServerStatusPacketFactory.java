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
      registry.setHandler(GetServerStatus.class, packet -> this.getServerStatus((GetServerStatus) packet));
   }

   /**
    * Gets a packet detailing a server status message.
    * <p>
    * Possible values for <code>status</code>:<br> 0 - Normal<br> 1 - Highly
    * populated<br> 2 - Full
    *
    * @return The server status packet.
    */
   protected byte[] getServerStatus(GetServerStatus packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4);
      mplew.writeShort(SendOpcode.SERVERSTATUS.getValue());
      mplew.writeShort(packet.status().getValue());
      return mplew.getPacket();
   }
}