package tools.packet.statusinfo;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetDojoInfo(String info) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_STATUS_INFO;
   }
}