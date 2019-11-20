package tools.packet.alliance

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class AllianceInvite(private var _allianceId: Int, private var _characterName: String) extends PacketInput {
  def allianceId: Int = _allianceId

  def characterName: String = _characterName

  override def opcode(): SendOpcode = SendOpcode.ALLIANCE_OPERATION
}