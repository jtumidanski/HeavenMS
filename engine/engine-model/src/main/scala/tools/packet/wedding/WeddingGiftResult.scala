package tools.packet.wedding

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class WeddingGiftResult(private var _mode: Byte, private var _itemNames: java.util.List[String],
                        private var _items: java.util.List[Item]) extends PacketInput {
  def mode: Byte = _mode

  def itemNames: java.util.List[String] = _itemNames

  def items: java.util.List[Item] = _items

  override def opcode(): SendOpcode = SendOpcode.WEDDING_GIFT_RESULT
}