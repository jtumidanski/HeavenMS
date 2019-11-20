package net.server.channel.packet

import net.server.MaplePacket

class UseSolomonPacket( private var _slot: Short,  private var _itemId: Int) extends MaplePacket {
     def slot: Short = _slot
     def itemId: Int = _itemId
}
