package tools.packet.storage;

import java.util.Collection;

import client.inventory.Item;
import constants.MapleInventoryType;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record StoreInStorage(Byte slots, MapleInventoryType inventoryType,
                             Collection<Item> items) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.STORAGE;
   }
}