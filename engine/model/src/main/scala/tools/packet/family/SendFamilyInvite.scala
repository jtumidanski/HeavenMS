package tools.packet.family

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SendFamilyInvite(private var _characterId: Int, private var _inviter: String) extends PacketInput {
  def characterId: Int = _characterId

  def inviter: String = _inviter

  override def opcode(): SendOpcode = SendOpcode.FAMILY_JOIN_REQUEST
}