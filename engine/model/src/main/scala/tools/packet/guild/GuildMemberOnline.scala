package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildMemberOnline(private var _guildId: Int, private var _characterId: Int, private var _online: Boolean) extends PacketInput {
  def guildId: Int = _guildId

  def characterId: Int = _characterId

  def online: Boolean = _online

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}