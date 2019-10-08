package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowSkillBookResult(private var _characterId: Int, private var _skillId: Int, private var _maxLevel: Int,
                          private var _canUse: Boolean, private var _success: Boolean) extends PacketInput {
  def characterId: Int = _characterId

  def skillId: Int = _skillId

  def maxLevel: Int = _maxLevel

  def canUse: Boolean = _canUse

  def success: Boolean = _success

  override def opcode(): SendOpcode = SendOpcode.SKILL_LEARN_ITEM_RESULT
}