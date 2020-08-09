package tools.packet.character.box;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class AddMatchCard implements PacketInput {
   private final MapleCharacter character;

   private final int amount;

   private final int type;

   public AddMatchCard(MapleCharacter character, int amount, int type) {
      this.character = character;
      this.amount = amount;
      this.type = type;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.UPDATE_CHAR_BOX;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public int getAmount() {
      return amount;
   }

   public int getType() {
      return type;
   }
}
