package tools.packet.character.npc

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemovePlayerNPC(private var _objectId: Int) extends PacketInput {
  def objectId: Int = _objectId

  override def opcode(): SendOpcode = SendOpcode.IMITATED_NPC_DATA
}