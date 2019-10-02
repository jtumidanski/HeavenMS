package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBossHP(private var _objectId: Int, private var _currentHP: Int, private var _maximumHP: Int,
                 private var _tagColor: Byte, private var _tagBackgroundColor: Byte) extends PacketInput {
  def objectId: Int = _objectId

  def currentHP: Int = _currentHP

  def maximumHP: Int = _maximumHP

  def tagColor: Byte = _tagColor

  def tagBackgroundColor: Byte = _tagBackgroundColor

  override def opcode(): SendOpcode = SendOpcode.FIELD_EFFECT
}