package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetClockTime(private var _hour: Int, private var _minute: Int, private var _second: Int) extends PacketInput {
  def hour: Int = _hour

  def minute: Int = _minute

  def second: Int = _second

  override def opcode(): SendOpcode = SendOpcode.CLOCK
}