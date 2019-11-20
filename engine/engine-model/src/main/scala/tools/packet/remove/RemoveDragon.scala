package tools.packet.remove

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoveDragon(private var _characterId: Int) extends PacketInput {
  def characterId: Int = _characterId

  override def opcode(): SendOpcode = SendOpcode.REMOVE_DRAGON
}