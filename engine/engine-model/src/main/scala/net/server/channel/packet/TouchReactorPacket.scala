package net.server.channel.packet

import net.server.MaplePacket

class TouchReactorPacket( private var _objectId: Int,  private var _isTouching: Boolean) extends MaplePacket {
     def objectId: Int = _objectId
     def isTouching: Boolean = _isTouching
}
