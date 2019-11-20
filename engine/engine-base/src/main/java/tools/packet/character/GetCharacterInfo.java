package tools.packet.character;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class GetCharacterInfo implements PacketInput {
   private MapleCharacter character;

   public GetCharacterInfo(MapleCharacter character) {
      this.character = character;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.CHAR_INFO;
   }

   public MapleCharacter getCharacter() {
      return character;
   }
}
