package tools.packet.cashshop.operation

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowCashInventory(private var _accountId: Int, private var _items: java.util.List[Item], private var _storageSlots: Byte, private var _characterSlots: Short) extends PacketInput {
  def accountId: Int = _accountId

  def items: java.util.List[Item] = _items

  def storageSlots: Byte = _storageSlots

  def characterSlots: Short = _characterSlots

  override def opcode(): SendOpcode = SendOpcode.CASHSHOP_OPERATION
}