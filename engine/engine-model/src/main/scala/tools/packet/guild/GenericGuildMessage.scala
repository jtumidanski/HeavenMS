package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GenericGuildMessage(private var _code: Byte) extends PacketInput {
  def code: Byte = _code

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}