package net.server.channel.packet.newyear

import net.server.MaplePacket

class BaseNewYearCardPacket( private var _reqMode: Byte) extends MaplePacket {
     def reqMode: Byte = _reqMode
}
