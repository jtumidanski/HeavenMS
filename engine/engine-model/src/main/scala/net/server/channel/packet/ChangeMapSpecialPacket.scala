package net.server.channel.packet

import net.server.MaplePacket

class ChangeMapSpecialPacket( private var _startWarp: String) extends MaplePacket {
     def startWarp: String = _startWarp
}
