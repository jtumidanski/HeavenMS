package net.server.channel.packet

import net.server.MaplePacket

class DistributeAPPacket( private var _number: Int) extends MaplePacket {
     def number: Int = _number
}
