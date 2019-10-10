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
      registry.setHandler(WrongPic.class, packet -> this.wrongPic((WrongPic) packet));
   }

   protected byte[] wrongPic(WrongPic packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.CHECK_SPW_RESULT.getValue());
      mplew.write(0);
      return mplew.getPacket();
   }
}