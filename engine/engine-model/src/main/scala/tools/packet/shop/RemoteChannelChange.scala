package tools.packet.shop

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoteChannelChange(private var _channelId: Byte) extends PacketInput {
  def channelId: Byte = _channelId

  override def opcode(): SendOpcode = SendOpcode.ENTRUSTED_SHOP_CHECK_RESULT
}