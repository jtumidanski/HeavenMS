package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.ChannelLoad;
import tools.packet.serverlist.ServerList;
import tools.packet.serverlist.ServerListEnd;

public class ServerListPacketFactory extends AbstractPacketFactory {
   private static ServerListPacketFactory instance;

   public static ServerListPacketFactory getInstance() {
      if (instance == null) {
         instance = new ServerListPacketFactory();
      }
      return instance;
   }

   private ServerListPacketFactory() {
      Handler.handle(ServerList.class).decorate(this::getServerList).register(registry);
      Handler.handle(ServerListEnd.class).decorate(this::getEndOfServerList).size(3).register(registry);
   }

   /**
    * Gets a packet detailing a server and its channels.
    */
   protected void getServerList(MaplePacketLittleEndianWriter writer, ServerList packet) {
      writer.write(packet.serverId());
      writer.writeMapleAsciiString(packet.serverName());
      writer.write(packet.flag());
      writer.writeMapleAsciiString(packet.eventMsg());
      writer.write(100); // rate modifier, don't ask O.O!
      writer.write(0); // event xp * 2.6 O.O!
      writer.write(100); // rate modifier, don't ask O.O!
      writer.write(0); // drop rate * 2.6
      writer.write(0);
      writer.write(packet.channelLoad().size());
      for (ChannelLoad ch : packet.channelLoad()) {
         writer.writeMapleAsciiString(packet.serverName() + "-" + ch.id());
         writer.writeInt(ch.capacity());
         writer.write(1);// nWorldID
         writer.write(ch.id() - 1);// nChannelID
         writer.writeBool(false);// bAdultChannel
      }
      writer.writeShort(0);
   }

   /**
    * Gets a packet saying that the server list is over.
    */
   protected void getEndOfServerList(MaplePacketLittleEndianWriter writer, ServerListEnd packet) {
      writer.write(0xFF);
   }
}