package net.server.channel.packet.family

import net.server.MaplePacket

class FamilyAddPacket( private var _toAdd: String) extends MaplePacket {
     def toAdd: String = _toAdd
}
