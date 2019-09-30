package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ServerIP) {
         return create(this::getServerIP, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
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