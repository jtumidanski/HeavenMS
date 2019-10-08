package tools.packet.pyramid

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PyramidGuage(private var _guage: Int) extends PacketInput {
  def guage: Int = _guage

  override def opcode(): SendOpcode = SendOpcode.PYRAMID_GAUGE
}