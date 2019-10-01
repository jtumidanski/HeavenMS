package tools.packet.spawn

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SpawnGuide(private var _spawn: Boolean) extends PacketInput {
  def spawn: Boolean = _spawn

  override def opcode(): SendOpcode = SendOpcode.SPAWN_GUIDE
}