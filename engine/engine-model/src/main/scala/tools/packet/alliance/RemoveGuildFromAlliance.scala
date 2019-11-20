package tools.packet.alliance

import net.opcodes.SendOpcode
import net.server.guild.MapleAlliance
import tools.packet.PacketInput

class RemoveGuildFromAlliance(private var _alliance: MapleAlliance, private var _expelledGuildId: Int, private var _worldId: Int) extends PacketInput {
  def alliance: MapleAlliance = _alliance

  def expelledGuildId: Int = _expelledGuildId

  def worldId: Int = _worldId

  override def opcode(): SendOpcode = SendOpcode.ALLIANCE_OPERATION
}