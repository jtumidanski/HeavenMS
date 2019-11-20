package net.server.channel.packet

import net.server.MaplePacket

class DistributeSPPacket( private var _skillId: Int) extends MaplePacket {
     def skillId: Int = _skillId
}
