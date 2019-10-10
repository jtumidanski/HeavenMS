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
      registry.setHandler(ServerIP.class, packet -> create(SendOpcode.SERVER_IP, this::getServerIP, packet));
   }

   /**
    * Gets a packet telling the client the IP of the channel server.
    *
    * @return The server IP packet.
    */
   protected void getServerIP(MaplePacketLittleEndianWriter writer, ServerIP packet) {
      writer.writeShort(0);
      byte[] addr = packet.inetAddress().getAddress();
      writer.write(addr);
      writer.writeShort(packet.port());
      writer.writeInt(packet.clientId());
      writer.write(new byte[]{0, 0, 0, 0, 0});
   }
}