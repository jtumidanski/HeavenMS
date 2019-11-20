package net.server.channel.packet.party

import net.server.MaplePacket

class BasePartyOperationPacket( private var _operation: Int) extends MaplePacket {
     def operation: Int = _operation
}
