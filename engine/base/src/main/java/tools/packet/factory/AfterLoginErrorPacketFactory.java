package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.AfterLoginError;

public class AfterLoginErrorPacketFactory extends AbstractPacketFactory {
   private static AfterLoginErrorPacketFactory instance;

   public static AfterLoginErrorPacketFactory getInstance() {
      if (instance == null) {
         instance = new AfterLoginErrorPacketFactory();
      }
      return instance;
   }

   private AfterLoginErrorPacketFactory() {
      registry.setHandler(AfterLoginError.class, packet -> this.getAfterLoginError((AfterLoginError) packet));
   }

   protected byte[] getAfterLoginError(AfterLoginError packet) {//same as above o.o
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(8);
      mplew.writeShort(SendOpcode.SELECT_CHARACTER_BY_VAC.getValue());
      mplew.writeShort(packet.reason());//using other types than stated above = CRASH
      return mplew.getPacket();
   }
}