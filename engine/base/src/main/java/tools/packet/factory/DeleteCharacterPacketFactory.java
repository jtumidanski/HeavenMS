package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.DeleteCharacter;

public class DeleteCharacterPacketFactory extends AbstractPacketFactory {
   private static DeleteCharacterPacketFactory instance;

   public static DeleteCharacterPacketFactory getInstance() {
      if (instance == null) {
         instance = new DeleteCharacterPacketFactory();
      }
      return instance;
   }

   private DeleteCharacterPacketFactory() {
      registry.setHandler(DeleteCharacter.class, packet -> create(SendOpcode.DELETE_CHAR_RESPONSE, this::deleteCharResponse, packet));
   }

   protected void deleteCharResponse(MaplePacketLittleEndianWriter writer, DeleteCharacter packet) {
      writer.writeInt(packet.characterId());
      writer.write(packet.state().getValue());
   }
}