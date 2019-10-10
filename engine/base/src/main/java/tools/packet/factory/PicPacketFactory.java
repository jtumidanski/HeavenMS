package tools.packet.factory;

import net.opcodes.SendOpcode;
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
      registry.setHandler(WrongPic.class, packet -> create(SendOpcode.CHECK_SPW_RESULT, this::wrongPic, packet, 3));
   }

   protected void wrongPic(MaplePacketLittleEndianWriter writer, WrongPic packet) {
      writer.write(0);
   }
}