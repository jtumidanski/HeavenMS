package net.server.channel.packet.alliance

import net.server.MaplePacket

class AlliancePlayerRankDataPacket( private var _playerId: Int,  private var _rankRaised: Boolean) extends AllianceOperationPacket {
     def playerId: Int = _playerId
     def rankRaised: Boolean = _rankRaised
}
