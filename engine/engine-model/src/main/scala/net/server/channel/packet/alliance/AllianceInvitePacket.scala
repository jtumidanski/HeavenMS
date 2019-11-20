package net.server.channel.packet.alliance

import net.server.MaplePacket

class AllianceInvitePacket( private var _guildName: String) extends AllianceOperationPacket {
     def guildName: String = _guildName
}
