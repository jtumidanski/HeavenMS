package tools.packet.shop;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdateHiredMerchantBox(int ownerId, int objectId, String description, int itemId,
                                     byte[] roomInto) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.UPDATE_HIRED_MERCHANT;
   }
}