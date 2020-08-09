package tools.packet.buff;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CancelForeignAbnormalStatus(Integer characterId, Long mask) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CANCEL_FOREIGN_BUFF;
   }
}
