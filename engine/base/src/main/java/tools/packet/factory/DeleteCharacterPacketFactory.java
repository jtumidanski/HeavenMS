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
      registry.setHandler(DeleteCharacter.class, packet -> this.deleteCharResponse((DeleteCharacter) packet));
   }

   protected byte[] deleteCharResponse(DeleteCharacter packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DELETE_CHAR_RESPONSE.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.state().getValue());
      return mplew.getPacket();
   }
}