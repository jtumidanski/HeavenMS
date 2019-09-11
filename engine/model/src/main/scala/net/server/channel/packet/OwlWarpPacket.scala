package net.server.channel.packet

import net.server.MaplePacket

class OwlWarpPacket( private var _ownerId: Int,  private var _mapId: Int) extends MaplePacket {
     def ownerId: Int = _ownerId
     def mapId: Int = _mapId
}
