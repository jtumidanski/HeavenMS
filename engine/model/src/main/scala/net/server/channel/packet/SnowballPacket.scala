package net.server.channel.packet

import net.server.MaplePacket

class SnowballPacket( private var _what: Int) extends MaplePacket {
     def what: Int = _what
}
