package tools.packet.showitemgaininchat

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowWheelsLeft(private var _remaining: Int) extends PacketInput {
  def remaining: Int = _remaining

  override def opcode(): SendOpcode = SendOpcode.SHOW_ITEM_GAIN_INCHAT
}