package net.server.channel.packet.duey

import net.server.MaplePacket

class BaseDueyPacket( private var _operation: Byte) extends MaplePacket {
     def operation: Byte = _operation
}
