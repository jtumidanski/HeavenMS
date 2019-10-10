package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.ChangeChannel;

public class ChangeChannelPacketFactory extends AbstractPacketFactory {
   private static ChangeChannelPacketFactory instance;

   public static ChangeChannelPacketFactory getInstance() {
      if (instance == null) {
         instance = new ChangeChannelPacketFactory();
      }
      return instance;
   }

   private ChangeChannelPacketFactory() {
      registry.setHandler(ChangeChannel.class, packet -> create(SendOpcode.CHANGE_CHANNEL, this::getChannelChange, packet));
   }

   /**
    * Gets a packet telling the client the IP of the new channel.
    *
    * @return The server IP packet.
    */
   protected void getChannelChange(MaplePacketLittleEndianWriter writer, ChangeChannel packet) {
      writer.write(1);
      byte[] addr = packet.inetAddress().getAddress();
      writer.write(addr);
      writer.writeShort(packet.port());
   }
}