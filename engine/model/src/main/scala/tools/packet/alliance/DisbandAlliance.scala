package tools.packet.alliance

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class DisbandAlliance(private var _allianceId: Int) extends PacketInput {
  def allianceId: Int = _allianceId

  override def opcode(): SendOpcode = SendOpcode.ALLIANCE_OPERATION
}