package tools.packet.showitemgaininchat

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowGainCard() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.SHOW_ITEM_GAIN_IN_CHAT
}