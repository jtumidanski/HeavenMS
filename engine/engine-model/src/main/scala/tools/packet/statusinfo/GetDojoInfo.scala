package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetDojoInfo(private var _info: String) extends PacketInput {
  def info: String = _info

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}