package tools.packet.messenger

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MessengerRemoveCharacter(private var _position: Int) extends PacketInput {
  def position: Int = _position

  override def opcode(): SendOpcode = SendOpcode.MESSENGER
}