package tools.packet.message;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ItemMegaphone(String message, Boolean whisper, Integer channel, Item item) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SERVER_MESSAGE;
   }
}