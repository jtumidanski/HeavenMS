package tools.packet.transfer.world;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record WorldTransferError(Integer error) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_CHECK_TRANSFER_WORLD_POSSIBLE_RESULT;
   }
}