package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.serverlist.ServerIP;

public class ServerIPPacketFactory extends AbstractPacketFactory {
   private static ServerIPPacketFactory instance;

   public static ServerIPPacketFactory getInstance() {
      if (instance == null) {
         instance = new ServerIPPacketFactory();
      }
      return instance;
   }

   private ServerIPPacketFactory() {
      registry.setHandler(ServerIP.class, packet -> this.getServerIP((ServerIP) packet));
   }

   /**
    * Gets a packet telling the client the IP of the channel server.
    *
    * @return The server IP packet.
    */
   protected byte[] getServerIP(ServerIP packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SERVER_IP.getValue());
      mplew.writeShort(0);
      byte[] addr = packet.inetAddress().getAddress();
      mplew.write(addr);
      mplew.writeShort(packet.port());
      mplew.writeInt(packet.clientId());
      mplew.write(new byte[]{0, 0, 0, 0, 0});
      return mplew.getPacket();
   }
}