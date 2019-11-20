package tools.packet.monster.carnival

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MonsterCarnivalPartyPoints(private var _team: Int, private var _currentPoints: Int, private var _totalPoints: Int) extends PacketInput {
  def team: Int = _team

  def currentPoints: Int = _currentPoints

  def totalPoints: Int = _totalPoints

  override def opcode(): SendOpcode = SendOpcode.MONSTER_CARNIVAL_PARTY_CP
}