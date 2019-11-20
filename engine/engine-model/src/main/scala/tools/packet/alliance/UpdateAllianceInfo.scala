package tools.packet.alliance

import net.opcodes.SendOpcode
import net.server.guild.MapleAlliance
import tools.packet.PacketInput

class UpdateAllianceInfo(private var _alliance: MapleAlliance, private var _worldId: Int) extends PacketInput {
  def alliance: MapleAlliance = _alliance

  def worldId: Int = _worldId

  override def opcode(): SendOpcode = SendOpcode.ALLIANCE_OPERATION
}