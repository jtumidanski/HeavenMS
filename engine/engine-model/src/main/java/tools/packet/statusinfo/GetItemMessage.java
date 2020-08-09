package tools.packet.statusinfo;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetItemMessage(Integer itemId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_STATUS_INFO;
   }
}