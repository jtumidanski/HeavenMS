package tools.packet.alliance

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetAlliancePlayerInfo( private var _allianceId: Int,  private var _playerId: Int) extends PacketInput {
     def allianceId: Int = _allianceId
     def playerId: Int = _playerId

  override def opcode(): SendOpcode = SendOpcode.ALLIANCE_OPERATION
}