package tools.packet

import net.opcodes.SendOpcode

class DeleteCharacter(private var _characterId: Int, private var _state: DeleteCharacterResponse) extends PacketInput {
  def characterId: Int = _characterId

  def state: DeleteCharacterResponse = _state

  override def opcode(): SendOpcode = SendOpcode.DELETE_CHAR_RESPONSE
}