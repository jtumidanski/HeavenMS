package net.server.channel.packet.family

import net.server.MaplePacket

class AcceptFamilyPacket( private var _inviterId: Int,  private var _accept: Boolean) extends MaplePacket {
     def inviterId: Int = _inviterId
     def accept: Boolean = _accept
}
