package tools.packet.cashshop;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowCash(Integer nxCredit, Integer maplePoint, Integer nxPrepaid) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.QUERY_CASH_RESULT;
   }
}