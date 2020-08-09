package tools.packet.character;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record DamageCharacter(int skill, int monsterIdFrom, int characterId, int damage, int fake, int direction,
                              boolean pgmr, int pgmr_1, boolean is_pg, int objectId, int xPosition,
                              int yPosition) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.DAMAGE_PLAYER;
   }
}