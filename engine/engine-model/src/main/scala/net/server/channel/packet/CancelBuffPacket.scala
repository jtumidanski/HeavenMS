package net.server.channel.packet

import net.server.MaplePacket

class CancelBuffPacket( private var _sourceId: Int) extends MaplePacket {
     def sourceId: Int = _sourceId
}
