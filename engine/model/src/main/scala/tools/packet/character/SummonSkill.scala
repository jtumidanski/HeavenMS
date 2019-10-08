package tools.packet.character

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SummonSkill(private var _characterId: Int, private var _summonSkillId: Int, private var _newStance: Int) extends PacketInput {
  def characterId: Int = _characterId

  def summonSkillId: Int = _summonSkillId

  def newStance: Int = _newStance

  override def opcode(): SendOpcode = SendOpcode.SUMMON_SKILL
}