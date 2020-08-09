package tools.packet.message;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GachaponMessage(Item item, String town, String characterName) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SERVER_MESSAGE;
   }
}