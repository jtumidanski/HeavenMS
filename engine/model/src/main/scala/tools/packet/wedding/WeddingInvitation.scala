package tools.packet.wedding

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class WeddingInvitation(private var _groom: String, private var _bride: String) extends PacketInput {
  def groom: String = _groom

  def bride: String = _bride

  override def opcode(): SendOpcode = SendOpcode.MARRIAGE_RESULT
}