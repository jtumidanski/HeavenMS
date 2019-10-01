package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildInvite(private var _guildId: Int, private var _characterName: String) extends PacketInput {
  def guildId: Int = _guildId

  def characterName: String = _characterName

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}