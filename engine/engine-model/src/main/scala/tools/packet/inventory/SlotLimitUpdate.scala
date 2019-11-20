package tools.packet.inventory

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SlotLimitUpdate(private var _inventoryType: Int, private var _newLimit: Int) extends PacketInput {
  def inventoryType: Int = _inventoryType

  def newLimit: Int = _newLimit

  override def opcode(): SendOpcode = SendOpcode.INVENTORY_GROW
}