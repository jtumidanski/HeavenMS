package tools.packet.cashshop.operation

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class DeleteCashItem(private var _item: Item) extends PacketInput {
  def item: Item = _item

  override def opcode(): SendOpcode = SendOpcode.CASHSHOP_OPERATION
}