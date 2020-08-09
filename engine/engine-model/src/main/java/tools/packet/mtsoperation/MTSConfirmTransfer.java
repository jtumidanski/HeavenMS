package tools.packet.mtsoperation;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MTSConfirmTransfer(int quantity, int position) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MTS_OPERATION;
   }
}