package tools.packet.monster

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class DamageSummon(private var _characterId: Int, private var _objectId: Int, private var _damage: Int,
                   private var _monsterIdFrom: Int) extends PacketInput {
  def characterId: Int = _characterId

  def objectId: Int = _objectId

  def damage: Int = _damage

  def monsterIdFrom: Int = _monsterIdFrom

  override def opcode(): SendOpcode = SendOpcode.DAMAGE_SUMMON
}