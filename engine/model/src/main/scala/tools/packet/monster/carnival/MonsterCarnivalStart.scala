package tools.packet.monster.carnival

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MonsterCarnivalStart(private var _team: Int, private var _freePoints: Int, private var _totalPoints: Int,
                           private var _teamFreePoints: Int, private var _teamTotalPoints: Int,
                           private var _oppositionFreePoints: Int, private var _oppositionTotalPoints: Int) extends PacketInput {
  def team: Int = _team

  def freePoints: Int = _freePoints

  def totalPoints: Int = _totalPoints

  def teamFreePoints: Int = _teamFreePoints

  def teamTotalPoints: Int = _teamTotalPoints

  def oppositionFreePoints: Int = _oppositionFreePoints

  def oppositionTotalPoints: Int = _oppositionTotalPoints

  override def opcode(): SendOpcode = SendOpcode.MONSTER_CARNIVAL_START
}