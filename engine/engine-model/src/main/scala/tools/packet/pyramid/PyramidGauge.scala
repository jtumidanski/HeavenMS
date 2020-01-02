package tools.packet.pyramid

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PyramidGauge(private var _gauge: Int) extends PacketInput {
  def gauge: Int = _gauge

  override def opcode(): SendOpcode = SendOpcode.PYRAMID_GAUGE
}