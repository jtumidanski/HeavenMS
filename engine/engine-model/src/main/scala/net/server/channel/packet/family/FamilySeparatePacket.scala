package net.server.channel.packet.family

import net.server.MaplePacket

class FamilySeparatePacket( private var _available: Boolean,  private var _characterId: Int) extends MaplePacket {
     def available: Boolean = _available
     def characterId: Int = _characterId
}
