package tools.packet.mtsoperation

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MTSWantedListingOver(private var _nx: Int, private var _items: Int) extends PacketInput {
  def nx: Int = _nx

  def items: Int = _items

  override def opcode(): SendOpcode = SendOpcode.MTS_OPERATION
}