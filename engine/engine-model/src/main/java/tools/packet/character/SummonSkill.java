package tools.packet.character;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SummonSkill(Integer characterId, Integer summonSkillId, Integer newStance) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SUMMON_SKILL;
   }
}