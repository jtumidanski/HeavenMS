package tools.packet.messenger

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MessengerInvite(private var _characterNameFrom: String, private var _messengerId: Int) extends PacketInput {
  def characterNameFrom: String = _characterNameFrom

  def messengerId: Int = _messengerId

  override def opcode(): SendOpcode = SendOpcode.MESSENGER
}