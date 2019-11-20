package tools.packet.cashshop.operation

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBoughtQuestItem(private var _itemId: Int) extends PacketInput {
  def itemId: Int = _itemId

  override def opcode(): SendOpcode = SendOpcode.CASHSHOP_OPERATION
}