package tools.packet.character.interaction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MerchantVisitorLeave(private var _slot: Int) extends PacketInput {
  def slot: Int = _slot

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}