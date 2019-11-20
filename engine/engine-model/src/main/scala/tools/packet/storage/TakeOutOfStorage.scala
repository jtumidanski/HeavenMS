package tools.packet.storage

import client.inventory.{Item, MapleInventoryType}
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class TakeOutOfStorage(private var _slots: Byte, private var _inventoryType: MapleInventoryType, private var _items: java.util.Collection[Item]) extends PacketInput {
  def slots: Byte = _slots

  def inventoryType: MapleInventoryType = _inventoryType

  def items: java.util.Collection[Item] = _items

  override def opcode(): SendOpcode = SendOpcode.STORAGE
}