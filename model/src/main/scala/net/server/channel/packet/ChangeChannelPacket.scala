package net.server.channel.packet

import net.server.MaplePacket

class ChangeChannelPacket(private var _channel: Int) extends MaplePacket {
     def channel: Int = _channel
}
