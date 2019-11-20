package net.server.channel.packet.fredrick

import net.server.MaplePacket

class BaseFrederickPacket( private var _operation: Byte) extends MaplePacket {
     def operation: Byte = _operation
}
