package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetDojoInfoMessage(private var _message: String) extends PacketInput {
  def message: String = _message

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}