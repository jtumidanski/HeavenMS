package net.server.channel.packet.guild

import net.server.MaplePacket

class DenyGuildRequestPacket(private var _characterName: String) extends MaplePacket {
     def characterName: String = _characterName
}
