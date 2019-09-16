package net.server.channel.packet

import net.server.MaplePacket

class InventorySortPacket( private var _inventoryType: Byte) extends MaplePacket {
     def inventoryType: Byte = _inventoryType
}
