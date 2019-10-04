package tools.packet.item.enhance

import net.opcodes.SendOpcode
import server.MaplePacketOpCodes
import tools.packet.PacketInput

class SendVegaScroll(private var _operation: MaplePacketOpCodes.VegaScroll) extends PacketInput {
  def operation: MaplePacketOpCodes.VegaScroll = _operation

  override def opcode(): SendOpcode = SendOpcode.VEGA_SCROLL
}