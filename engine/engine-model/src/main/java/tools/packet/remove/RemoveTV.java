package tools.packet.remove;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveTV() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.REMOVE_TV;
   }
}