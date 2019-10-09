package tools.packet.character.interaction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MerchantChat(private var _message: String, private var _slot: Byte) extends PacketInput {
  def message: String = _message

  def slot: Byte = _slot

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}