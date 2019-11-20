package tools.packet.showitemgaininchat

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowItemGainInChat(private var _itemId: Int, private var _quantity: Short) extends PacketInput {
  def itemId: Int = _itemId

  def quantity: Short = _quantity

  override def opcode(): SendOpcode = SendOpcode.SHOW_ITEM_GAIN_INCHAT
}