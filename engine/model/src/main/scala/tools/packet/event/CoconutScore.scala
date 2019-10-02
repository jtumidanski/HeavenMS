package tools.packet.event

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CoconutScore(private var _firstTeam: Int, private var _secondTeam: Int) extends PacketInput {
  def firstTeam: Int = _firstTeam

  def secondTeam: Int = _secondTeam

  override def opcode(): SendOpcode = SendOpcode.COCONUT_SCORE
}