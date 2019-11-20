package tools.packet.guild

import client.database.data.GuildData
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowGuildRanks(private var _npcId: Int, private var _ranks: java.util.List[GuildData]) extends PacketInput {
  def npcId: Int = _npcId

  def ranks: java.util.List[GuildData] = _ranks

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}