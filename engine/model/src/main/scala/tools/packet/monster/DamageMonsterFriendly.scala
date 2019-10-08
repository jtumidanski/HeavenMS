package tools.packet.monster

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class DamageMonsterFriendly(private var _objectId: Int, private var _damage: Int, private var _remainingHp: Int, private var _maximumHp: Int) extends PacketInput {
  def objectId: Int = _objectId

  def damage: Int = _damage

  def remainingHp: Int = _remainingHp

  def maximumHp: Int = _maximumHp

  override def opcode(): SendOpcode = SendOpcode.DAMAGE_MONSTER
}