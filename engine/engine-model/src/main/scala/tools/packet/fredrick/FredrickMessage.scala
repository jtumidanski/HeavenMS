package tools.packet.fredrick

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class FredrickMessage(private var _operation: Byte) extends PacketInput {
  def operation: Byte = _operation

  override def opcode(): SendOpcode = SendOpcode.FREDRICK_MESSAGE
}