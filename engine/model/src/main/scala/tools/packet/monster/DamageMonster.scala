package tools.packet.monster

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class DamageMonster(private var _objectId: Int, private var _damage: Int) extends PacketInput {
  def objectId: Int = _objectId

  def damage: Int = _damage

  override def opcode(): SendOpcode = SendOpcode.DAMAGE_MONSTER
}