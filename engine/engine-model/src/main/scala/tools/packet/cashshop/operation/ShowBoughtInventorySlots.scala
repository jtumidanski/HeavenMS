package tools.packet.cashshop.operation

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBoughtInventorySlots(private var _inventoryType: Int, private var _slots: Short) extends PacketInput {
  def inventoryType: Int = _inventoryType

  def slots: Short = _slots

  override def opcode(): SendOpcode = SendOpcode.CASH_SHOP_OPERATION
}