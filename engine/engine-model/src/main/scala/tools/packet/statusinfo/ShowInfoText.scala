package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowInfoText(private var _text: String) extends PacketInput {
  def text: String = _text

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}