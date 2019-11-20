package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.CharacterName;

public class CharacterNameResponsePacketFactory extends AbstractPacketFactory {
   private static CharacterNameResponsePacketFactory instance;

   public static CharacterNameResponsePacketFactory getInstance() {
      if (instance == null) {
         instance = new CharacterNameResponsePacketFactory();
      }
      return instance;
   }

   private CharacterNameResponsePacketFactory() {
      Handler.handle(CharacterName.class).decorate(this::charNameResponse).register(registry);
   }

   protected void charNameResponse(MaplePacketLittleEndianWriter writer, CharacterName packet) {
      writer.writeMapleAsciiString(packet.characterName());
      writer.write(packet.nameUsed() ? 1 : 0);
   }
}