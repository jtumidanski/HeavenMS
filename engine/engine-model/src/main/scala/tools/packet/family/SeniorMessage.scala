package tools.packet.family

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SeniorMessage(private var _characterName: String) extends PacketInput {
  def characterName: String = _characterName

  override def opcode(): SendOpcode = SendOpcode.FAMILY_JOIN_ACCEPTED
}