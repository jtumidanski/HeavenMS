package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowTitleEarned(String message) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SCRIPT_PROGRESS_MESSAGE;
   }
}