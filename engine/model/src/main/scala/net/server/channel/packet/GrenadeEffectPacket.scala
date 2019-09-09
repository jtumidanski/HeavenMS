package net.server.channel.packet

import net.server.MaplePacket

class GrenadeEffectPacket( private var _x: Int,  private var _y: Int,  private var _keyDown: Int,  private var _skillId: Int) extends MaplePacket {
     def x: Int = _x
     def y: Int = _y
     def keyDown: Int = _keyDown
     def skillId: Int = _skillId
}
