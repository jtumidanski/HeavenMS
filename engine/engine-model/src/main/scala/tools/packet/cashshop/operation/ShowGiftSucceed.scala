package tools.packet.cashshop.operation

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowGiftSucceed(private var _to: String, private var _itemId: Int, private var _count: Short,
                      private var _price: Int) extends PacketInput {
  def to: String = _to

  def itemId: Int = _itemId

  def count: Short = _count

  def price: Int = _price

  override def opcode(): SendOpcode = SendOpcode.CASH_SHOP_OPERATION
}