package tools.packet.factory;

import net.opcodes.SendOpcode;
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
      registry.setHandler(CharacterName.class, packet -> create(SendOpcode.CHAR_NAME_RESPONSE, this::charNameResponse, packet));
   }

   protected void charNameResponse(MaplePacketLittleEndianWriter writer, CharacterName packet) {
      writer.writeMapleAsciiString(packet.characterName());
      writer.write(packet.nameUsed() ? 1 : 0);
   }
}