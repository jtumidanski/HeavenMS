package tools.packet.monster.carnival

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MonsterCarnivalPlayerSummoned(private var _name: String, private var _tab: Int, private var _number: Int) extends PacketInput {
  def name: String = _name

  def tab: Int = _tab

  def number: Int = _number

  override def opcode(): SendOpcode = SendOpcode.MONSTER_CARNIVAL_SUMMON
}