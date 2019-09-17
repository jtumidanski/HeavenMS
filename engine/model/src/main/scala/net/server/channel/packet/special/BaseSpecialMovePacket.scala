package net.server.channel.packet.special

import java.awt.Point

import net.server.MaplePacket

class BaseSpecialMovePacket(private var _skillId: Int, private var _skillLevel: Int, private var _position: Point) extends MaplePacket {
  def skillId: Int = _skillId

  def skillLevel: Int = _skillLevel

  def position: Point = _position
}
