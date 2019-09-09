package net.server.channel.packet

import net.server.MaplePacket

class PlayerLoggedInPacket(private var _characterId: Int) extends MaplePacket {
     def characterId: Int = _characterId
}
