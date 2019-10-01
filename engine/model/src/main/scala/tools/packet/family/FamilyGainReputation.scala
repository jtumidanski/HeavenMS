package tools.packet.family

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class FamilyGainReputation(private var _gain: Int, private var _characterNameFrom: String) extends PacketInput {
  def gain: Int = _gain

  def characterNameFrom: String = _characterNameFrom

  override def opcode(): SendOpcode = SendOpcode.FAMILY_REP_GAIN
}