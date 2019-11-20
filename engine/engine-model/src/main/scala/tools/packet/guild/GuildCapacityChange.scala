package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildCapacityChange(private var _guildId: Int, private var _capacity: Int) extends PacketInput {
  def guildId: Int = _guildId

  def capacity: Int = _capacity

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}