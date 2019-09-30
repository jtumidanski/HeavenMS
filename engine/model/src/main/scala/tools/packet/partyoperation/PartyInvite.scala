package tools.packet.partyoperation

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PartyInvite(private var _partyId: Int, private var _fromCharacterName: String) extends PacketInput {
  def partyId: Int = _partyId

  def fromCharacterName: String = _fromCharacterName

  override def opcode(): SendOpcode = SendOpcode.PARTY_OPERATION
}