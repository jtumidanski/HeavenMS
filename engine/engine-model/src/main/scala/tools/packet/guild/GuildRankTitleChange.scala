package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildRankTitleChange(private var _guildId: Int, private var _ranks: Array[String]) extends PacketInput {
  def guildId: Int = _guildId

  def ranks: Array[String] = _ranks

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}