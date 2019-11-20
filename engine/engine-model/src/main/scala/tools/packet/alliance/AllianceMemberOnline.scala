package tools.packet.alliance

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class AllianceMemberOnline(private var _allianceId: Int, private var _guildId: Int, private var _characterId: Int, private var _online: Boolean) extends PacketInput {
  def allianceId: Int = _allianceId

  def guildId: Int = _guildId

  def characterId: Int = _characterId

  def online: Boolean = _online

  override def opcode(): SendOpcode = SendOpcode.ALLIANCE_OPERATION
}