package net.server.channel.packet

import net.server.MaplePacket

class MonsterBookCoverPacket( private var _coverId: Int) extends MaplePacket {
     def coverId: Int = _coverId
}
