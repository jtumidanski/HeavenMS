package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetItemMessage(private var _itemId: Int) extends PacketInput {
  def itemId: Int = _itemId

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}