package tools.packet.party

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdatePartyMemberHp(private var _characterId: Int, private var _currentHp: Int, private var _maximumHp: Int) extends PacketInput {
  def characterId: Int = _characterId

  def currentHp: Int = _currentHp

  def maximumHp: Int = _maximumHp

  override def opcode(): SendOpcode = SendOpcode.UPDATE_PARTY_MEMBER_HP
}