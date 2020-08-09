package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowSkillEffect(Integer characterId, Integer skillId, Integer level, Byte flags, Integer speed,
                              Byte direction) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SKILL_EFFECT;
   }
}