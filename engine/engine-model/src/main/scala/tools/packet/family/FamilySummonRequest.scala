package tools.packet.family

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class FamilySummonRequest(private var _familyName: String, private var _characterNameFrom: String) extends PacketInput {
  def familyName: String = _familyName

  def characterNameFrom: String = _characterNameFrom

  override def opcode(): SendOpcode = SendOpcode.FAMILY_SUMMON_REQUEST
}