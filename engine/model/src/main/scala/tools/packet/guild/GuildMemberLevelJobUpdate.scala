package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildMemberLevelJobUpdate(private var _guildId: Int, private var _characterId: Int, private var _level: Int,
                                private var _jobId: Int) extends PacketInput {
  def guildId: Int = _guildId

  def characterId: Int = _characterId

  def level: Int = _level

  def jobId: Int = _jobId

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}