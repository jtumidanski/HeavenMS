package tools.packet.buff;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GiveForeignChairSkillEffect(Integer characterId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GIVE_FOREIGN_BUFF;
   }
}
