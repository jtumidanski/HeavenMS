package net.server.channel.packet.ring

import net.server.MaplePacket

class BaseRingPacket( private var _mode: Byte) extends MaplePacket {
     def mode: Byte = _mode
}
