package tools.packet.family

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class FamilyJoinResponse(private var _accepted: Boolean, private var _characterNameAdded: String) extends PacketInput {
  def accepted: Boolean = _accepted

  def characterNameAdded: String = _characterNameAdded

  override def opcode(): SendOpcode = SendOpcode.FAMILY_JOIN_REQUEST_RESULT
}