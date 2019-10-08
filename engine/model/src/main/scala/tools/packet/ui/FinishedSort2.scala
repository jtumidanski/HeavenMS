package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class FinishedSort2(private var _inventory: Int) extends PacketInput {
  def inventory: Int = _inventory

  override def opcode(): SendOpcode = SendOpcode.SORT_ITEM_RESULT
}