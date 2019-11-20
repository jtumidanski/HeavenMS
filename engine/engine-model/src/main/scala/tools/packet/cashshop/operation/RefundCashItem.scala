package tools.packet.cashshop.operation

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RefundCashItem(private var _item: Item, private var _maplePoints: Int) extends PacketInput {
  def item: Item = _item

  def maplePoints: Int = _maplePoints

  override def opcode(): SendOpcode = SendOpcode.CASHSHOP_OPERATION
}