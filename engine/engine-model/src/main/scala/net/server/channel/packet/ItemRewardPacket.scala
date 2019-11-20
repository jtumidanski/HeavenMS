package net.server.channel.packet

import net.server.MaplePacket

class ItemRewardPacket( private var _slot: Byte,  private var _itemId: Int) extends MaplePacket {
     def slot: Byte = _slot
     def itemId: Int = _itemId
}
