package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CustomShowBossHP(private var _call: Byte, private var _objectId: Int, private var _currentHP: Long,
                       private var _maximumHP: Long, private var _tagColor: Byte, private var _tagBackgroundColor: Byte) extends PacketInput {
  def call: Byte = _call

  def objectId: Int = _objectId

  def currentHP: Long = _currentHP

  def maximumHP: Long = _maximumHP

  def tagColor: Byte = _tagColor

  def tagBackgroundColor: Byte = _tagBackgroundColor

  override def opcode(): SendOpcode = SendOpcode.FIELD_EFFECT
}