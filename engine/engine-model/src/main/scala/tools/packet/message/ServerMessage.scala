package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ServerMessage(private var _message: String) extends PacketInput {
  def message: String = _message

  override def opcode(): SendOpcode = SendOpcode.SERVER_MESSAGE
}