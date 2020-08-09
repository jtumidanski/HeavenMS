package tools.packet.inventory; 
import java.util.List;

import client.inventory.ModifyInventory;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ModifyInventoryPacket( Boolean updateTick,  List<ModifyInventory> modifications) implements PacketInput {
  @Override
  public SendOpcode opcode() {
    return SendOpcode.INVENTORY_OPERATION;
  }
}