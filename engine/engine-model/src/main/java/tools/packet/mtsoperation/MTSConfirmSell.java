package tools.packet.mtsoperation;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MTSConfirmSell() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MTS_OPERATION;
   }
}