package tools.packet.character;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdateSkill(int skillId, int level, int masterLevel, long expiration) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.UPDATE_SKILLS;
   }
}