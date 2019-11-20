package net.server.channel.packet

import net.server.MaplePacket

class UseMapleLifePacket( private var _name: String) extends MaplePacket {
     def name: String = _name
}
