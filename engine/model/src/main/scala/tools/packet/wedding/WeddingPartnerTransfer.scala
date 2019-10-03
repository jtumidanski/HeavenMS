package tools.packet.wedding

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class WeddingPartnerTransfer(private var _partnerId: Int, private var _mapId: Int) extends PacketInput {
  def partnerId: Int = _partnerId

  def mapId: Int = _mapId

  override def opcode(): SendOpcode = SendOpcode.NOTIFY_MARRIED_PARTNER_MAP_TRANSFER
}