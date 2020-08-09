package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CancelSkill(Integer fromCharacterId, Integer skillId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CANCEL_SKILL_EFFECT;
   }
}