package net.server.channel.packet.alliance

import net.server.MaplePacket

class AllianceRankDataPacket( private var _ranks: Array[String]) extends AllianceOperationPacket {
     def ranks: Array[String] = _ranks
}
