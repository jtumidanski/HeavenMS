package net.server.channel.packet

import net.server.MaplePacket

class BeholderPacket( private var _objectId: Int,  private var _skillId: Int) extends MaplePacket {
     def objectId: Int = _objectId
     def skillId: Int = _skillId
}
