package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowBlockedUI(Integer theType) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.BLOCKED_SERVER;
   }
}