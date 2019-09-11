package net.server.channel.packet

import net.server.MaplePacket

class CoconutPacket( private var _id: Int) extends MaplePacket {
     def id: Int = _id
}
