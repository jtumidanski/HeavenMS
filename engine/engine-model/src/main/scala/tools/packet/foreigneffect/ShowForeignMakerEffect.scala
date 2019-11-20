package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowForeignMakerEffect(private var _characterId: Int, private var _success: Boolean) extends PacketInput {
  def characterId: Int = _characterId

  def success: Boolean = _success

  override def opcode(): SendOpcode = SendOpcode.SHOW_FOREIGN_EFFECT
}