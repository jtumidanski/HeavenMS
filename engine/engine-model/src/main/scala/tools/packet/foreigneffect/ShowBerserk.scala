package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBerserk(private var _characterId: Int, private var _skillLevel: Int, private var _berserk: Boolean) extends PacketInput {
  def characterId: Int = _characterId

  def skillLevel: Int = _skillLevel

  def berserk: Boolean = _berserk

  override def opcode(): SendOpcode = SendOpcode.SHOW_FOREIGN_EFFECT
}