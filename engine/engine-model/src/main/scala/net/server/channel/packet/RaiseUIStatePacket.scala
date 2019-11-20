package net.server.channel.packet

import net.server.MaplePacket

class RaiseUIStatePacket( private var _questId: Int) extends MaplePacket {
     def questId: Int = _questId
}
