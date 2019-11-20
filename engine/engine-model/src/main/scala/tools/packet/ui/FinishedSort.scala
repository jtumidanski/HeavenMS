package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class FinishedSort(private var _inventory: Int) extends PacketInput {
  def inventory: Int = _inventory

  override def opcode(): SendOpcode = SendOpcode.GATHER_ITEM_RESULT
}