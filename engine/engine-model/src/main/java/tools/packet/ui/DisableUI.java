package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record DisableUI(Boolean enable) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.DISABLE_UI;
   }
}