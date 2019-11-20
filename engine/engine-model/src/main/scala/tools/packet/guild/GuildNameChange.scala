package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildNameChange(private var _characterId: Int, private var _guildName: String) extends PacketInput {
  def characterId: Int = _characterId

  def guildName: String = _guildName

  override def opcode(): SendOpcode = SendOpcode.GUILD_NAME_CHANGED
}