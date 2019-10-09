package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CancelChair(private var _itemId: Int) extends PacketInput {
  def itemId: Int = _itemId

  override def opcode(): SendOpcode = SendOpcode.CANCEL_CHAIR
}