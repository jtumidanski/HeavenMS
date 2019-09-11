package net.server.channel.packet

import net.server.MaplePacket

class RemoteGachaponPacket( private var _ticket: Int,  private var _gachaponId: Int) extends MaplePacket {
     def ticket: Int = _ticket
     def gachaponId: Int = _gachaponId
}
