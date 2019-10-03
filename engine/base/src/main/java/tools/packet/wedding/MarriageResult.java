package tools.packet.wedding;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class MarriageResult implements PacketInput {
   private int marriageId;

   private MapleCharacter character;

   private boolean wedding;

   public MarriageResult(int marriageId, MapleCharacter character, boolean wedding) {
      this.marriageId = marriageId;
      this.character = character;
      this.wedding = wedding;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.MARRIAGE_RESULT;
   }

   public int getMarriageId() {
      return marriageId;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public boolean isWedding() {
      return wedding;
   }
}
