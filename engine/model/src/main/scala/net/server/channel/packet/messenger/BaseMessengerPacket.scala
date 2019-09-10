package net.server.channel.packet.messenger

import net.server.MaplePacket

class BaseMessengerPacket( private var _mode: Byte) extends MaplePacket {
     def mode: Byte = _mode
}
