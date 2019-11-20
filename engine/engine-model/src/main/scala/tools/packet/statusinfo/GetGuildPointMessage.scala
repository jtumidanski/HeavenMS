package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetGuildPointMessage(private var _change: Int) extends PacketInput {
  def change: Int = _change

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}