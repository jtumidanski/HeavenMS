package net.server.channel.packet

import net.server.MaplePacket

class DoorPacket( private var _ownerId: Int,  private var _backWarp: Boolean) extends MaplePacket {
     def ownerId: Int = _ownerId
     def backWarp: Boolean = _backWarp
}
