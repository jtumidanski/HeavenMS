package tools.packet.storage;

import java.util.Collection;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ArrangeStorage(Byte slots, Collection<Item> items) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.STORAGE;
   }
}