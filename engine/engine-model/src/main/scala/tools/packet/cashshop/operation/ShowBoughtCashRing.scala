package tools.packet.cashshop.operation

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBoughtCashRing(private var _ring: Item, private var _recipient: String, private var _accountId: Int) extends PacketInput {
  def ring: Item = _ring

  def recipient: String = _recipient

  def accountId: Int = _accountId

  override def opcode(): SendOpcode = SendOpcode.CASH_SHOP_OPERATION
}