package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildNotice(private var _guildId: Int, private var _notice: String) extends PacketInput {
  def guildId: Int = _guildId

  def notice: String = _notice

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}