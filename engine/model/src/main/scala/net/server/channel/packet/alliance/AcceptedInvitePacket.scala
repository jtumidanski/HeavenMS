package net.server.channel.packet.alliance

import net.server.MaplePacket

class AcceptedInvitePacket( private var _allianceId: Int,  private var _recruitingGuild: String) extends AllianceOperationPacket {
     def allianceId: Int = _allianceId
     def recruitingGuild: String = _recruitingGuild
}
