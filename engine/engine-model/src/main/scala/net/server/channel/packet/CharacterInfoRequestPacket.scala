package net.server.channel.packet

import net.server.MaplePacket

class CharacterInfoRequestPacket( private var _characterId: Int) extends MaplePacket {
     def characterId: Int = _characterId
}
