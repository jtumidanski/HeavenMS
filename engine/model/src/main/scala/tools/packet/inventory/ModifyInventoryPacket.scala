package tools.packet.inventory

import client.inventory.ModifyInventory
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ModifyInventoryPacket(private var _updateTick: Boolean, private var _modifications: java.util.List[ModifyInventory]) extends PacketInput {
  //noinspection MutatorLikeMethodIsParameterless
  def updateTick: Boolean = _updateTick

  def modifications: java.util.List[ModifyInventory] = _modifications

  override def opcode(): SendOpcode = SendOpcode.INVENTORY_OPERATION
}