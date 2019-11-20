package tools.packet.messenger

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MessengerChat(private var _text: String) extends PacketInput {
  def text: String = _text

  override def opcode(): SendOpcode = SendOpcode.MESSENGER
}