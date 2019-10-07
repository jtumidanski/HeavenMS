package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class YellowTip(private var _tip: String) extends PacketInput {
  def tip: String = _tip

  override def opcode(): SendOpcode = SendOpcode.SET_WEEK_EVENT_MESSAGE
}