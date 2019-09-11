package net.server.channel.packet

import net.server.MaplePacket

class CouponCodePacket( private var _code: String) extends MaplePacket {
     def code: String = _code
}
