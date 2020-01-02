package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ChatText(private var _characterIdFrom: Int, private var _text: String, private var _gm: Boolean,
               private var _show: Int) extends PacketInput {
  def characterIdFrom: Int = _characterIdFrom

  def text: String = _text

  def gm: Boolean = _gm

  def show: Int = _show

  override def opcode(): SendOpcode = SendOpcode.CHAT_TEXT
}