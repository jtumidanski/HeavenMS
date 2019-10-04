package tools.packet.attack

import net.opcodes.SendOpcode
import net.server.channel.handlers.SummonAttackEntry
import tools.packet.PacketInput

class SummonAttack(private var _characterId: Int, private var _summonObjectId: Int, private var _direction: Byte,
                   private var _damage: java.util.List[SummonAttackEntry]) extends PacketInput {
  def characterId: Int = _characterId

  def summonObjectId: Int = _summonObjectId

  def direction: Byte = _direction

  def damage: java.util.List[SummonAttackEntry] = _damage

  override def opcode(): SendOpcode = SendOpcode.SUMMON_ATTACK
}