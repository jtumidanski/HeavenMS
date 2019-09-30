package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowForeignEffect(private var _characterId: Int, private var _effect: Int) extends PacketInput {
  def characterId: Int = _characterId

  def effect: Int = _effect

  override def opcode(): SendOpcode = SendOpcode.SHOW_FOREIGN_EFFECT
}