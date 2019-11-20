package tools.packet.character

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SetAutoMpPot(private var _itemId: Int) extends PacketInput {
  def itemId: Int = _itemId

  override def opcode(): SendOpcode = SendOpcode.AUTO_MP_POT
}