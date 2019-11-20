package tools.packet.pet

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PetChat(private var _characterId: Int, private var _index: Byte, private var _act: Int, private var _text: String) extends PacketInput {
  def characterId: Int = _characterId

  def index: Byte = _index

  def act: Int = _act

  def text: String = _text

  override def opcode(): SendOpcode = SendOpcode.PET_CHAT
}