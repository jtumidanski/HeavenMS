package tools.packet.remove

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemovePlayer(private var _characterId: Int) extends PacketInput {
  def characterId: Int = _characterId

  override def opcode(): SendOpcode = SendOpcode.REMOVE_PLAYER_FROM_MAP
}