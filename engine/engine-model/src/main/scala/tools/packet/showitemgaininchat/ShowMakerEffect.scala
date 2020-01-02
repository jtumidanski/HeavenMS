package tools.packet.showitemgaininchat

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowMakerEffect(private var _success: Boolean) extends PacketInput {
  def success: Boolean = _success

  override def opcode(): SendOpcode = SendOpcode.SHOW_ITEM_GAIN_IN_CHAT
}