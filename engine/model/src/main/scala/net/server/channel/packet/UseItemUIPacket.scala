package net.server.channel.packet

import net.server.MaplePacket

class UseItemUIPacket(private var _inventoryType: Byte, private var _slot: Short, private var _itemId: Int) extends MaplePacket {
     def inventoryType: Byte = _inventoryType
     def slot: Short = _slot
     def itemId: Int = _itemId
}
