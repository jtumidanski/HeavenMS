package net.server.channel.packet

import net.server.MaplePacket

class MonsterBombPacket( private var _objectId: Int) extends MaplePacket {
     def objectId: Int = _objectId
}
