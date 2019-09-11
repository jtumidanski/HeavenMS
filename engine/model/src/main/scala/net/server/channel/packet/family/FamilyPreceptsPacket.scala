package net.server.channel.packet.family

import net.server.MaplePacket

class FamilyPreceptsPacket( private var _newPrecepts: String) extends MaplePacket {
     def newPrecepts: String = _newPrecepts
}
