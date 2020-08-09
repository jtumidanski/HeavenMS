package tools.packet.transfer.name;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record NameChangeCancel(Boolean success) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CANCEL_NAME_CHANGE_RESULT;
   }
}