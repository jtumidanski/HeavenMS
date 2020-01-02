package tools.packet.factory;

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
      Handler.handle(ChangeChannel.class).decorate(this::getChannelChange).register(registry);
   }

   /**
    * Gets a packet telling the client the IP of the new channel.
    */
   protected void getChannelChange(MaplePacketLittleEndianWriter writer, ChangeChannel packet) {
      writer.write(1);
      byte[] addr = packet.inetAddress().getAddress();
      writer.write(addr);
      writer.writeShort(packet.port());
   }
}