package tools.packet.factory;

import tools.Randomizer;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.GuestTOS;

public class GuestLoginPacketFactory extends AbstractPacketFactory {
   private static GuestLoginPacketFactory instance;

   public static GuestLoginPacketFactory getInstance() {
      if (instance == null) {
         instance = new GuestLoginPacketFactory();
      }
      return instance;
   }

   private GuestLoginPacketFactory() {
      Handler.handle(GuestTOS.class).decorate(this::sendGuestTOS).register(registry);
   }

   protected void sendGuestTOS(MaplePacketLittleEndianWriter writer, GuestTOS packet) {
      writer.writeShort(0x100);
      writer.writeInt(Randomizer.nextInt(999999));
      writer.writeLong(0);
      writer.writeLong(getTime(-2));
      writer.writeLong(getTime(System.currentTimeMillis()));
      writer.writeInt(0);
      writer.writeMapleAsciiString("http://maplesolaxia.com");
   }
}