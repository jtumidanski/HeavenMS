package tools.packet.alliance

import net.opcodes.SendOpcode
import net.server.guild.MapleAlliance
import tools.packet.PacketInput

class AddGuildToAlliance(private var _alliance: MapleAlliance, private var _newGuildId: Int, private var _worldId: Int) extends PacketInput {
  def alliance: MapleAlliance = _alliance

  def newGuildId: Int = _newGuildId

  def worldId: Int = _worldId

  override def opcode(): SendOpcode = SendOpcode.ALLIANCE_OPERATION
}