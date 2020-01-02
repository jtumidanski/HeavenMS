package tools.packet.cashshop.operation

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowNameChangeSuccess(private var _item: Item, private var _accountId: Int) extends PacketInput {
  def item: Item = _item

  def accountId: Int = _accountId

  override def opcode(): SendOpcode = SendOpcode.CASH_SHOP_OPERATION
}