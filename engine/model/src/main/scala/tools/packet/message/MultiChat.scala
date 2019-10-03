package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MultiChat(private var _name: String, private var _text: String, private var _mode: Int) extends PacketInput {
  def name: String = _name

  def text: String = _text

  def mode: Int = _mode

  override def opcode(): SendOpcode = SendOpcode.MULTICHAT
}