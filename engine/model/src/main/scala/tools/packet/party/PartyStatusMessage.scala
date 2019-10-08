package tools.packet.party

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PartyStatusMessage(private var _message: Int, private var _fromCharacterName: Option[String]) extends PacketInput {
  def message: Int = _message

  def fromCharacterName: Option[String] = _fromCharacterName

  def this(_message: Int) = {
    this(_message, Option.empty)
  }

  override def opcode(): SendOpcode = SendOpcode.PARTY_OPERATION
}