package net.server.channel.packet

import net.server.MaplePacket

class UseChairPacket( private var _itemId: Int) extends MaplePacket {
     def itemId: Int = _itemId
}
