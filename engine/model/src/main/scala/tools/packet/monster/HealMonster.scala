package tools.packet.monster

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class HealMonster(private var _objectId: Int, private var _heal: Int, private var _currentHp: Int, private var _maximumHp: Int) extends PacketInput {
  def objectId: Int = _objectId

  def heal: Int = _heal

  def currentHp: Int = _currentHp

  def maximumHp: Int = _maximumHp

  override def opcode(): SendOpcode = SendOpcode.DAMAGE_MONSTER
}