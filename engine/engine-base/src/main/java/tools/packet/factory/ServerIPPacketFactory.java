package tools.packet.factory;

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
      Handler.handle(ServerIP.class).decorate(this::getServerIP).register(registry);
   }

   /**
    * Gets a packet telling the client the IP of the channel server.
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