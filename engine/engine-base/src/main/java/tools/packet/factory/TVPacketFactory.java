package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.EnableTV;
import tools.packet.SendTV;

public class TVPacketFactory extends AbstractPacketFactory {
   private static TVPacketFactory instance;

   public static TVPacketFactory getInstance() {
      if (instance == null) {
         instance = new TVPacketFactory();
      }
      return instance;
   }

   private TVPacketFactory() {
      Handler.handle(EnableTV.class).decorate(this::enableTV).size(7).register(registry);
      Handler.handle(SendTV.class).decorate(this::sendTV).register(registry);
   }

   protected void enableTV(MaplePacketLittleEndianWriter writer, EnableTV packet) {
      writer.writeInt(0);
      writer.write(0);
   }

   /**
    * Sends MapleTV
    */
   protected void sendTV(MaplePacketLittleEndianWriter writer, SendTV packet) {
      writer.write(packet.getPartner() != null ? 3 : 1);
      writer.write(packet.getType()); //Heart = 2  Star = 1  Normal = 0
      addCharLook(writer, packet.getCharacter(), false);
      writer.writeMapleAsciiString(packet.getCharacter().getName());
      if (packet.getPartner() != null) {
         writer.writeMapleAsciiString(packet.getPartner().getName());
      } else {
         writer.writeShort(0);
      }
      for (int i = 0; i < packet.getMessages().size(); i++) {
         if (i == 4 && packet.getMessages().get(4).length() > 15) {
            writer.writeMapleAsciiString(packet.getMessages().get(4).substring(0, 15));
         } else {
            writer.writeMapleAsciiString(packet.getMessages().get(i));
         }
      }
      writer.writeInt(1337); // time limit shit lol 'Your thing still start in blah blah seconds'
      if (packet.getPartner() != null) {
         addCharLook(writer, packet.getPartner(), false);
      }
   }
}