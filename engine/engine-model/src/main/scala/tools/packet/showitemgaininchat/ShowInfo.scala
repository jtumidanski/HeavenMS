package tools.packet.showitemgaininchat

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowInfo(private var _path: String) extends PacketInput {
  def path: String = _path

  override def opcode(): SendOpcode = SendOpcode.SHOW_ITEM_GAIN_IN_CHAT
}