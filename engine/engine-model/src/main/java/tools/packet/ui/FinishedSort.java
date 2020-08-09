package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record FinishedSort(int inventory) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GATHER_ITEM_RESULT;
   }
}