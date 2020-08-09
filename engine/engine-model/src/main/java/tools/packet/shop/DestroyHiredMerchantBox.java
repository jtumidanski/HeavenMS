package tools.packet.shop;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record DestroyHiredMerchantBox(Integer ownerId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.DESTROY_HIRED_MERCHANT;
   }
}