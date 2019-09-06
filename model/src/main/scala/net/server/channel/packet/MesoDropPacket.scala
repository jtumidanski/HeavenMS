package net.server.channel.packet

import net.server.MaplePacket

class MesoDropPacket( private var _meso: Int) extends MaplePacket {
     def meso: Int = _meso
}
