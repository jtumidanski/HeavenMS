package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildMemberLeft(private var _guildId: Int, private var _characterId: Int, private var _characterName: String,
                      private var _expelled: Boolean) extends PacketInput {
  def guildId: Int = _guildId

  def characterId: Int = _characterId

  def characterName: String = _characterName

  def expelled: Boolean = _expelled

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}