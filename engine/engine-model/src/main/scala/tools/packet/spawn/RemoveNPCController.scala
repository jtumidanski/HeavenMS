package tools.packet.spawn

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoveNPCController(private var _objectId: Int) extends PacketInput {
  def objectId: Int = _objectId

  override def opcode(): SendOpcode = SendOpcode.SPAWN_NPC_REQUEST_CONTROLLER
}