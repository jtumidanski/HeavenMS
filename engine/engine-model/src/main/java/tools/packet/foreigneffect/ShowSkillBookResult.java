package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowSkillBookResult(Integer characterId, Integer skillId, Integer maxLevel, Boolean canUse,
                                  Boolean success) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SKILL_LEARN_ITEM_RESULT;
   }
}