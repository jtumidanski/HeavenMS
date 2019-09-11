package net.server.channel.packet.family

import net.server.MaplePacket

class FamilySummonResponsePacket( private var _familyName: String,  private var _accept: Boolean) extends MaplePacket {
     def familyName: String = _familyName
     def accept: Boolean = _accept
}
