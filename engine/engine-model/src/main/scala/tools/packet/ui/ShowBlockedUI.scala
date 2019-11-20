package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBlockedUI(private var _type: Int) extends PacketInput {
  def theType: Int = _type

  override def opcode(): SendOpcode = SendOpcode.BLOCKED_SERVER
}