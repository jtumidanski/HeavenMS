package net.server.channel.packet.buddy

import net.server.MaplePacket

class BaseBuddyPacket( private var _mode: Int) extends MaplePacket {
     def mode: Int = _mode
}
