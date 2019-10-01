package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class NewGuildMember(private var _guildId: Int, private var _characterId: Int, private var _name: String,
                     private var _jobId: Int, private var _level: Int, private var _guildRank: Int,
                     private var _online: Boolean) extends PacketInput {
  def guildId: Int = _guildId

  def characterId: Int = _characterId

  def name: String = _name

  def jobId: Int = _jobId

  def level: Int = _level

  def guildRank: Int = _guildRank

  def online: Boolean = _online

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}