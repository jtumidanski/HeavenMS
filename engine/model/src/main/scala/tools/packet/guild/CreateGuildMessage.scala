package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CreateGuildMessage(private var _masterCharacterName: String, private var _guildName: String) extends PacketInput {
  def masterCharacterName: String = _masterCharacterName

  def guildName: String = _guildName

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}