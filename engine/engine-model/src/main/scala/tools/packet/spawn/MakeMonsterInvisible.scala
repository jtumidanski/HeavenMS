package tools.packet.spawn

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MakeMonsterInvisible(private var _monsterObjectId: Int) extends PacketInput {
  def monsterObjectId: Int = _monsterObjectId

  override def opcode(): SendOpcode = SendOpcode.SPAWN_MONSTER_CONTROL
}