package tools.packet.wedding;

import java.util.List;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record WeddingGiftResult(Byte mode, List<String> itemNames, List<Item> items) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.WEDDING_GIFT_RESULT;
   }
}