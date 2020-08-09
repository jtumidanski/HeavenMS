package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record LockUI(Boolean enable) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.LOCK_UI;
   }
}