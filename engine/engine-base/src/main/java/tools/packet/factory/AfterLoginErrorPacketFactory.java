package tools.packet.factory;

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
      Handler.handle(AfterLoginError.class).decorate(this::getAfterLoginError).size(8).register(registry);
   }

   protected void getAfterLoginError(MaplePacketLittleEndianWriter writer, AfterLoginError packet) {//same as above o.o
      writer.writeShort(packet.reason());//using other types than stated above = CRASH
   }
}