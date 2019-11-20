package tools.packet.alliance

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ChangeAllianceRankTitles(private var _allianceId: Int, private var _ranks: Array[String]) extends PacketInput {
  def allianceId: Int = _allianceId

  def ranks: Array[String] = _ranks

  override def opcode(): SendOpcode = SendOpcode.ALLIANCE_OPERATION
}