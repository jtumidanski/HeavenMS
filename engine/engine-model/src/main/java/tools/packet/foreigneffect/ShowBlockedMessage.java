package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowBlockedMessage(Integer theType) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.BLOCKED_MAP;
   }
}