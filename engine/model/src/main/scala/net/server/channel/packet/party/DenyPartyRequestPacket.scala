package net.server.channel.packet.party

import net.server.MaplePacket

class DenyPartyRequestPacket( private var _message: String) extends MaplePacket {
     def message: String = _message
}
