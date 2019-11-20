package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class TrembleEffect(private var _type: Int, private var _delay: Int) extends PacketInput {
  def theType: Int = _type

  def delay: Int = _delay

  override def opcode(): SendOpcode = SendOpcode.FIELD_EFFECT
}