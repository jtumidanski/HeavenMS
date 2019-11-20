package tools.packet

import net.opcodes.SendOpcode

class SelectWorld(private var _worldId: Int) extends PacketInput {
  def worldId: Int = _worldId

  override def opcode(): SendOpcode = SendOpcode.LAST_CONNECTED_WORLD
}