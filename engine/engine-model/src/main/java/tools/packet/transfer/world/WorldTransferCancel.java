package tools.packet.transfer.world;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record WorldTransferCancel(Boolean success) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CANCEL_TRANSFER_WORLD_RESULT;
   }
}