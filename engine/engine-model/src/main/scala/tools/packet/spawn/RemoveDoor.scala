package tools.packet.spawn

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoveDoor(private var _ownerId: Int, private var _town: Boolean) extends PacketInput {
  def ownerId: Int = _ownerId

  def town: Boolean = _town

  override def opcode(): SendOpcode = SendOpcode.REMOVE_DOOR
}