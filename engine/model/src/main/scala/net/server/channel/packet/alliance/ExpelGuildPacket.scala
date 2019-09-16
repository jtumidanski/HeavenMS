package net.server.channel.packet.alliance

import net.server.MaplePacket

class ExpelGuildPacket( private var _guildId: Int,  private var _allianceId: Int) extends AllianceOperationPacket {
     def guildId: Int = _guildId
     def allianceId: Int = _allianceId
}
