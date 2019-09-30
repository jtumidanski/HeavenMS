package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.ChangeChannel;
import tools.packet.PacketInput;

public class ChangeChannelPacketFactory extends AbstractPacketFactory {
   private static ChangeChannelPacketFactory instance;

   public static ChangeChannelPacketFactory getInstance() {
      if (instance == null) {
         instance = new ChangeChannelPacketFactory();
      }
      return instance;
   }

   private ChangeChannelPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ChangeChannel) {
         return create(this::getChannelChange, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Gets a packet telling the client the IP of the new channel.
    *
    * @return The server IP packet.
    */
   protected byte[] getChannelChange(ChangeChannel packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CHANGE_CHANNEL.getValue());
      mplew.write(1);
      byte[] addr = packet.inetAddress().getAddress();
      mplew.write(addr);
      mplew.writeShort(packet.port());
      return mplew.getPacket();
   }
}