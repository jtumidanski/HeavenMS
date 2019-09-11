package net.server.channel.packet.pet

import net.server.MaplePacket

class PetAutoPotPacket( private var _slot: Short,  private var _itemId: Int) extends MaplePacket {
     def slot: Short = _slot
     def itemId: Int = _itemId
}
