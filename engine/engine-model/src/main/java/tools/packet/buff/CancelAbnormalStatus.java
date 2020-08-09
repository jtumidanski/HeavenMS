package tools.packet.buff;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CancelAbnormalStatus(Long mask) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CANCEL_BUFF;
   }
}
