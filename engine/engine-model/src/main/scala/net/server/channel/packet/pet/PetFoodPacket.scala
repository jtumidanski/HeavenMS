package net.server.channel.packet.pet

import net.server.MaplePacket

class PetFoodPacket( private var _timestamp: Int,  private var _position: Short,  private var _itemId: Int) extends MaplePacket {
     def timestamp: Int = _timestamp
     def position: Short = _position
     def itemId: Int = _itemId
}
