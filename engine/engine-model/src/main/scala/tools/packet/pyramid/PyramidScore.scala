package tools.packet.pyramid

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PyramidScore(private var _score: Byte, private var _exp: Int) extends PacketInput {
  def score: Byte = _score

  def exp: Int = _exp

  override def opcode(): SendOpcode = SendOpcode.PYRAMID_SCORE
}