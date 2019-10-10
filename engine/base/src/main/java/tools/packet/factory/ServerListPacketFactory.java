package tools.packet.factory;

import net.opcodes.SendOpcode;
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
      registry.setHandler(ServerList.class, packet -> this.getServerList((ServerList) packet));
      registry.setHandler(ServerListEnd.class, packet -> this.getEndOfServerList((ServerListEnd) packet));
   }

   /**
    * Gets a packet detailing a server and its channels.
    *
    * @return The server info packet.
    */
   protected byte[] getServerList(ServerList packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SERVERLIST.getValue());
      mplew.write(packet.serverId());
      mplew.writeMapleAsciiString(packet.serverName());
      mplew.write(packet.flag());
      mplew.writeMapleAsciiString(packet.eventMsg());
      mplew.write(100); // rate modifier, don't ask O.O!
      mplew.write(0); // event xp * 2.6 O.O!
      mplew.write(100); // rate modifier, don't ask O.O!
      mplew.write(0); // drop rate * 2.6
      mplew.write(0);
      mplew.write(packet.channelLoad().size());
      for (ChannelLoad ch : packet.channelLoad()) {
         mplew.writeMapleAsciiString(packet.serverName() + "-" + ch.id());
         mplew.writeInt(ch.capacity());

         // thanks GabrielSin for this channel packet structure part
         mplew.write(1);// nWorldID
         mplew.write(ch.id() - 1);// nChannelID
         mplew.writeBool(false);// bAdultChannel
      }
      mplew.writeShort(0);
      return mplew.getPacket();
   }

   /**
    * Gets a packet saying that the server list is over.
    *
    * @return The end of server list packet.
    */
   protected byte[] getEndOfServerList(ServerListEnd packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.SERVERLIST.getValue());
      mplew.write(0xFF);
      return mplew.getPacket();
   }
}