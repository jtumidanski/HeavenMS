package tools.packet.storage;

import java.util.Collection;

import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record TakeOutOfStorage(Byte slots, MapleInventoryType inventoryType,
                               Collection<Item> items) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.STORAGE;
   }
}