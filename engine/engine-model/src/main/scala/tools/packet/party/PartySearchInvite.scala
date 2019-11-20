package tools.packet.party

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PartySearchInvite(private var _partyId: Int, private var _fromCharacterName: String) extends PacketInput {
  def partyId: Int = _partyId

  def fromCharacterName: String = _fromCharacterName

  override def opcode(): SendOpcode = SendOpcode.PARTY_OPERATION
}