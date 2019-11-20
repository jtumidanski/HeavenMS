package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetClock(private var _time: Int) extends PacketInput {
  def time: Int = _time

  override def opcode(): SendOpcode = SendOpcode.CLOCK
}