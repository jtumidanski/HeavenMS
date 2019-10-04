package tools.packet.character.box

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoveMiniGameBox(private var _characterId: Int) extends PacketInput {
  def characterId: Int = _characterId

  override def opcode(): SendOpcode = SendOpcode.UPDATE_CHAR_BOX
}