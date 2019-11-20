package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowChair(private var _characterId: Int, private var _itemId: Int) extends PacketInput {
  def characterId: Int = _characterId

  def itemId: Int = _itemId

  override def opcode(): SendOpcode = SendOpcode.SHOW_CHAIR
}