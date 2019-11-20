package tools.packet.messenger

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MessengerNote(private var _text: String, private var _mode: Int, private var _mode2: Int) extends PacketInput {
  def text: String = _text

  def mode: Int = _mode

  def mode2: Int = _mode2

  override def opcode(): SendOpcode = SendOpcode.MESSENGER
}