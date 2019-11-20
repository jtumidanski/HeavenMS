package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateGuildPoints(private var _guildId: Int, private var _points: Int) extends PacketInput {
  def guildId: Int = _guildId

  def points: Int = _points

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}