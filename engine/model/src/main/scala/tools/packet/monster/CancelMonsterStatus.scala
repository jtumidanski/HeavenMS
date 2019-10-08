package tools.packet.monster

import client.status.MonsterStatus
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CancelMonsterStatus(private var _objectId: Int, private var _stats: java.util.Map[MonsterStatus, java.lang.Integer]) extends PacketInput {
  def objectId: Int = _objectId

  def stats: java.util.Map[MonsterStatus, java.lang.Integer] = _stats

  override def opcode(): SendOpcode = SendOpcode.CANCEL_MONSTER_STATUS
}