package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record YellowTip(String tip) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SET_WEEK_EVENT_MESSAGE;
   }
}