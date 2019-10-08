package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowSkillEffect(private var _characterId: Int, private var _skillId: Int, private var _level: Int,
                      private var _flags: Byte, private var _speed: Int, private var _direction: Byte) extends PacketInput {
  def characterId: Int = _characterId

  def skillId: Int = _skillId

  def level: Int = _level

  def flags: Byte = _flags

  def speed: Int = _speed

  def direction: Byte = _direction

  override def opcode(): SendOpcode = SendOpcode.SKILL_EFFECT
}