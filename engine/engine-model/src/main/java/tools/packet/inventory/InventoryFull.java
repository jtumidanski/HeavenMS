package tools.packet.inventory;

import java.util.Collections;
import java.util.List;

import client.inventory.ModifyInventory;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record InventoryFull(Boolean updateTick, List<ModifyInventory> modifications) implements PacketInput {
   public InventoryFull() {
      this(true, Collections.emptyList());
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.INVENTORY_OPERATION;
   }
}