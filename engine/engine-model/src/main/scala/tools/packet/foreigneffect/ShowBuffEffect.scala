package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBuffEffect(private var _characterId: Int, private var _skillId: Int, private var _effectId: Int,
                     private var _direction: Byte) extends PacketInput {
  def characterId: Int = _characterId

  def skillId: Int = _skillId

  def effectId: Int = _effectId

  def direction: Byte = _direction

  override def opcode(): SendOpcode = SendOpcode.SHOW_FOREIGN_EFFECT
}