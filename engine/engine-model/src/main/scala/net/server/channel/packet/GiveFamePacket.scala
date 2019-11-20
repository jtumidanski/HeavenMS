package net.server.channel.packet

import net.server.MaplePacket

class GiveFamePacket( private var _characterId: Int,  private var _mode: Int) extends MaplePacket {
     def characterId: Int = _characterId
     def mode: Int = _mode
}
