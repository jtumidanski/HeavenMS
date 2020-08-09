package tools.packet.buff;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CancelForeignChairSkillEffect(Integer characterId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CANCEL_FOREIGN_BUFF;
   }
}
