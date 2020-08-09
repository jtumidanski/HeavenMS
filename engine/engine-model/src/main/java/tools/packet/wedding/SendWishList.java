package tools.packet.wedding;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SendWishList() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MARRIAGE_RESULT;
   }
}