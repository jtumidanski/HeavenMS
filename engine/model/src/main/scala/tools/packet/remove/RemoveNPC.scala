package tools.packet.remove

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoveNPC(private var _objectId: Int) extends PacketInput {
  def objectId: Int = _objectId

  override def opcode(): SendOpcode = SendOpcode.REMOVE_NPC
}