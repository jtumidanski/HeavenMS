package tools.packet.fredrick

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetFredrick(private var _operation: Byte) extends PacketInput {
  def operation: Byte = _operation

  override def opcode(): SendOpcode = SendOpcode.FREDRICK
}