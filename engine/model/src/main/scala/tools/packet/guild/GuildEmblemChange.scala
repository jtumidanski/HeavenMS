package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildEmblemChange(private var _guildId: Int, private var _background: Short, private var _backgroundColor: Byte,
                        private var _logo: Short, private var _logoColor: Byte) extends PacketInput {
  def guildId: Int = _guildId

  def background: Short = _background

  def backgroundColor: Byte = _backgroundColor

  def logo: Short = _logo

  def logoColor: Byte = _logoColor

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}