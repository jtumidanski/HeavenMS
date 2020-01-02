package tools.packet.cashshop.operation

import net.opcodes.SendOpcode
import tools.packet.{Gift, PacketInput}

class ShowGifts(private var _gifts: java.util.List[Gift]) extends PacketInput {
  def gifts: java.util.List[Gift] = _gifts

  override def opcode(): SendOpcode = SendOpcode.CASH_SHOP_OPERATION
}