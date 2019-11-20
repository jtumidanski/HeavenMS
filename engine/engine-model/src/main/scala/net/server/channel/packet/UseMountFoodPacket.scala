package net.server.channel.packet

import net.server.MaplePacket

class UseMountFoodPacket( private var _position: Short,  private var _itemId: Int) extends MaplePacket {
     def position: Short = _position
     def itemId: Int = _itemId
}
