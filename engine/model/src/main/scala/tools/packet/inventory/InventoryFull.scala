package tools.packet.inventory

import java.util.Collections

import client.inventory.ModifyInventory
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class InventoryFull() extends PacketInput {
  //noinspection MutatorLikeMethodIsParameterless
  def updateTick: Boolean = true

  def modifications: java.util.List[ModifyInventory] = Collections.emptyList()

  override def opcode(): SendOpcode = SendOpcode.INVENTORY_OPERATION
}