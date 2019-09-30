package tools.packet.storage

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ArrangeStorage(private var _slots: Byte, private var _items: java.util.Collection[Item]) extends PacketInput {
  def slots: Byte = _slots

  def items: java.util.Collection[Item] = _items

  override def opcode(): SendOpcode = SendOpcode.STORAGE
}