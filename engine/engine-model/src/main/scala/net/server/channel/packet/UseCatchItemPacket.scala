package net.server.channel.packet

import net.server.MaplePacket

class UseCatchItemPacket( private var _itemId: Int,  private var _monsterId: Int) extends MaplePacket {
     def itemId: Int = _itemId
     def monsterId: Int = _monsterId
}
