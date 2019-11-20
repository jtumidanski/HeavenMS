package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildDisband(private var _guildId: Int) extends PacketInput {
  def guildId: Int = _guildId

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}