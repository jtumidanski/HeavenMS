package tools.packet.monster.carnival

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MonsterCarnivalPointObtained(private var _currentPoints: Int, private var _totalPoints: Int) extends PacketInput {
  def currentPoints: Int = _currentPoints

  def totalPoints: Int = _totalPoints

  override def opcode(): SendOpcode = SendOpcode.MONSTER_CARNIVAL_OBTAINED_CP
}