package tools.packet.character;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SkillCoolDown(Integer skillId, Integer time) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.COOL_DOWN;
   }
}