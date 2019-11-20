package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CancelSkill(private var _fromCharacterId: Int, private var _skillId: Int) extends PacketInput {
  def fromCharacterId: Int = _fromCharacterId

  def skillId: Int = _skillId

  override def opcode(): SendOpcode = SendOpcode.CANCEL_SKILL_EFFECT
}