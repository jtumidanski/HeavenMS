package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildMemberChangeRank(private var _guildId: Int, private var _characterId: Int, private var _guildRank: Int) extends PacketInput {
  def guildId: Int = _guildId

  def characterId: Int = _characterId

  def guildRank: Int = _guildRank

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}