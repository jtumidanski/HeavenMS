package tools.packet.guild

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ResponseGuildMessage(private var _code: Byte, private var _targetName: String) extends PacketInput {
  def code: Byte = _code

  def targetName: String = _targetName

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}