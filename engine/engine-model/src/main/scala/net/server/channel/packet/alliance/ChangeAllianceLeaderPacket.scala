package net.server.channel.packet.alliance

import net.server.MaplePacket

class ChangeAllianceLeaderPacket( private var _playerId: Int) extends AllianceOperationPacket {
     def playerId: Int = _playerId
}
