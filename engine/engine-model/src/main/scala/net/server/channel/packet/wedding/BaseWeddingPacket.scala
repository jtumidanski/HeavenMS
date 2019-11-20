package net.server.channel.packet.wedding

import net.server.MaplePacket

class BaseWeddingPacket( private var _mode: Byte) extends MaplePacket {
     def mode: Byte = _mode
}
