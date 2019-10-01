package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GuildQuestWaitingNotice(private var _channel: Byte, private var _position: Int) extends PacketInput {
  def channel: Byte = _channel

  def position: Int = _position

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}