package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBlockedMessage(private var _type: Int) extends PacketInput {
  def theType: Int = _type

  override def opcode(): SendOpcode = SendOpcode.BLOCKED_MAP
}