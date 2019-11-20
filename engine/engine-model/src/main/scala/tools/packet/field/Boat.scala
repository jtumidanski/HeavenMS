package tools.packet.field

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class Boat(private var _type: Boolean) extends PacketInput {
  def theType: Boolean = _type

  override def opcode(): SendOpcode = SendOpcode.CONTI_STATE
}