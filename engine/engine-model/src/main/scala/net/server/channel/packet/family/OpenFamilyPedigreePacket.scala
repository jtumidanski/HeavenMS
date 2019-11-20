package net.server.channel.packet.family

import net.server.MaplePacket

class OpenFamilyPedigreePacket( private var _characterName: String) extends MaplePacket {
     def characterName: String = _characterName
}
