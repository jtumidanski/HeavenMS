package tools.packet.character.box

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UseChalkboard(private var _characterId: Int, private var _close: Boolean, private var _text: String) extends PacketInput {
  def characterId: Int = _characterId

  def close: Boolean = _close

  def text: String = _text

  override def opcode(): SendOpcode = SendOpcode.CHALKBOARD
}