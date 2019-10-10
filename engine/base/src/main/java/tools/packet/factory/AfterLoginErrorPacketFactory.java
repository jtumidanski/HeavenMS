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
      registry.setHandler(AfterLoginError.class, packet -> create(SendOpcode.SELECT_CHARACTER_BY_VAC, this::getAfterLoginError, packet, 8));
   }

   protected void getAfterLoginError(MaplePacketLittleEndianWriter writer, AfterLoginError packet) {//same as above o.o
      writer.writeShort(packet.reason());//using other types than stated above = CRASH
   }
}