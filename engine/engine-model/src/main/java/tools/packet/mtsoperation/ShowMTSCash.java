package tools.packet.mtsoperation;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowMTSCash(Integer maplePoint, Integer nxPrepaid) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MTS_OPERATION;
   }
}