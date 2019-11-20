package tools.packet.mtsoperation

import net.opcodes.SendOpcode
import server.MTSItemInfo
import tools.packet.PacketInput

class GetNotYetSoldMTSInventory(private var _items: java.util.List[MTSItemInfo]) extends PacketInput {
  def items: java.util.List[MTSItemInfo] = _items

  override def opcode(): SendOpcode = SendOpcode.MTS_OPERATION
}