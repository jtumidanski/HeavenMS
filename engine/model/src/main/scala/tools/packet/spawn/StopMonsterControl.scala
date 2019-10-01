package tools.packet.spawn

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class StopMonsterControl(private var _monsterId: Int) extends PacketInput {
  def monsterId: Int = _monsterId

  override def opcode(): SendOpcode = SendOpcode.SPAWN_MONSTER_CONTROL
}