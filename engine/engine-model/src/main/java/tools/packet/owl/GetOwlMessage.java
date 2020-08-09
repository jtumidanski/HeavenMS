package tools.packet.owl;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetOwlMessage(Integer message) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOP_LINK_RESULT;
   }
}