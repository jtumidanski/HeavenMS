package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record DisableMiniMap() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ADMIN_RESULT;
   }
}