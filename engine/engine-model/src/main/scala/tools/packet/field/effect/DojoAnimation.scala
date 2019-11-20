package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class DojoAnimation(private var _firstByte: Byte, private var _animation: String) extends PacketInput {
  def firstByte: Byte = _firstByte

  def animation: String = _animation

  override def opcode(): SendOpcode = SendOpcode.FIELD_EFFECT
}