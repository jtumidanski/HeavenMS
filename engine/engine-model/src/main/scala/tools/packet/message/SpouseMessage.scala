package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SpouseMessage(private var _fiance: String, private var _text: String, private var _spouse: Boolean) extends PacketInput {
  def fiance: String = _fiance

  def text: String = _text

  def spouse: Boolean = _spouse

  override def opcode(): SendOpcode = SendOpcode.SPOUSE_CHAT
}