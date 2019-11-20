package net.server.channel.packet.alliance

import net.server.MaplePacket

class DenyAllianceRequestPacket(private var _inviterName: String, private var _guildName: String) extends MaplePacket {
     def inviterName: String = _inviterName
     def guildName: String = _guildName
}
