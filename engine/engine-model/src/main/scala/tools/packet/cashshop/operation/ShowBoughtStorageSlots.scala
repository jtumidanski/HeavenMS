package tools.packet.cashshop.operation

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBoughtStorageSlots(private var _slots: Short) extends PacketInput {
  def slots: Short = _slots

  override def opcode(): SendOpcode = SendOpcode.CASH_SHOP_OPERATION
}