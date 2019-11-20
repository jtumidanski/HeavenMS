package tools.packet.factory;

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
      Handler.handle(DeleteCharacter.class).decorate(this::deleteCharResponse).register(registry);
   }

   protected void deleteCharResponse(MaplePacketLittleEndianWriter writer, DeleteCharacter packet) {
      writer.writeInt(packet.characterId());
      writer.write(packet.state().getValue());
   }
}