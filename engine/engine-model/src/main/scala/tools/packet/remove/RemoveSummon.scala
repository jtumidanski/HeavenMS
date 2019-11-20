package tools.packet.remove

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoveSummon(private var _ownerId: Int, private var _objectId: Int, private var _animated: Boolean) extends PacketInput {
  def ownerId: Int = _ownerId

  def objectId: Int = _objectId

  def animated: Boolean = _animated

  override def opcode(): SendOpcode = SendOpcode.REMOVE_SPECIAL_MAPOBJECT
}