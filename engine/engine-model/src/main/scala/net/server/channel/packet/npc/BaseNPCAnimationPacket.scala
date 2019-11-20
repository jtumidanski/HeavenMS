package net.server.channel.packet.npc

import net.server.MaplePacket

class BaseNPCAnimationPacket( private var _available: Int) extends MaplePacket {
     def available: Int = _available
}
