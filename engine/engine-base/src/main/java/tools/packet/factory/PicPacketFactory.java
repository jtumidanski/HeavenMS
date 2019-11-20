package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.WrongPic;

public class PicPacketFactory extends AbstractPacketFactory {
   private static PicPacketFactory instance;

   public static PicPacketFactory getInstance() {
      if (instance == null) {
         instance = new PicPacketFactory();
      }
      return instance;
   }

   private PicPacketFactory() {
      Handler.handle(WrongPic.class).decorate(this::wrongPic).size(3).register(registry);
   }

   protected void wrongPic(MaplePacketLittleEndianWriter writer, WrongPic packet) {
      writer.write(0);
   }
}