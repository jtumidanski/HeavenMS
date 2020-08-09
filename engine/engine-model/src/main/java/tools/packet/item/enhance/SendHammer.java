package tools.packet.item.enhance;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SendHammer(Integer used) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.VICIOUS_HAMMER;
   }
}