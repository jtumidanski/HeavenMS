package tools.packet

import net.opcodes.SendOpcode

class CharacterName(private var _characterName: String, private var _nameUsed: Boolean) extends PacketInput {
  def characterName: String = _characterName

  def nameUsed: Boolean = _nameUsed

  override def opcode(): SendOpcode = SendOpcode.CHAR_NAME_RESPONSE
}