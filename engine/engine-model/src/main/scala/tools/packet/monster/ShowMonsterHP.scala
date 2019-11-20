package tools.packet.monster

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowMonsterHP(private var _objectId: Int, private var _remainingHpPercentage: Int) extends PacketInput {
  def objectId: Int = _objectId

  def remainingHpPercentage: Int = _remainingHpPercentage

  override def opcode(): SendOpcode = SendOpcode.SHOW_MONSTER_HP
}