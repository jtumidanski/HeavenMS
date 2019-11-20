package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBuffEffectWithLevel(private var _characterId: Int, private var _skillId: Int, private var _skillLevel: Int,
                              private var _effectId: Int, private var _direction: Byte) extends PacketInput {
  def characterId: Int = _characterId

  def skillId: Int = _skillId

  def skillLevel: Int = _skillLevel

  def effectId: Int = _effectId

  def direction: Byte = _direction

  override def opcode(): SendOpcode = SendOpcode.SHOW_FOREIGN_EFFECT
}