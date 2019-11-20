package tools.packet.alliance

import net.opcodes.SendOpcode
import net.server.guild.MapleAlliance
import tools.packet.PacketInput

class GetAllianceInfo(private var _alliance: MapleAlliance) extends PacketInput {
  def alliance: MapleAlliance = _alliance

  override def opcode(): SendOpcode = SendOpcode.ALLIANCE_OPERATION
}