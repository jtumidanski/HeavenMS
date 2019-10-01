package tools.packet.monster.carnival

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MonsterCarnivalPlayerDied(private var _name: String, private var _lostPoints: Int, private var _team: Int) extends PacketInput {
  def name: String = _name

  def lostPoints: Int = _lostPoints

  def team: Int = _team

  override def opcode(): SendOpcode = SendOpcode.MONSTER_CARNIVAL_DIED
}