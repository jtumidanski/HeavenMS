package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record FinishedSort2(int inventory) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SORT_ITEM_RESULT;
   }
}