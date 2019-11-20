package tools.packet.character

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SetAutoHpPot(private var _itemId: Int) extends PacketInput {
  def itemId: Int = _itemId

  override def opcode(): SendOpcode = SendOpcode.AUTO_HP_POT
}