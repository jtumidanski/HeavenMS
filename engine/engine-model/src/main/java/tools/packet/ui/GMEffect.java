package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GMEffect(Integer theType, Byte mode) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ADMIN_RESULT;
   }
}