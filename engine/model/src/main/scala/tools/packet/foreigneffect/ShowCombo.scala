package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowCombo(private var _count: Int) extends PacketInput {
  def count: Int = _count

  override def opcode(): SendOpcode = SendOpcode.SHOW_COMBO
}