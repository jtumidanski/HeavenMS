package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class StopClock() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.STOP_CLOCK
}