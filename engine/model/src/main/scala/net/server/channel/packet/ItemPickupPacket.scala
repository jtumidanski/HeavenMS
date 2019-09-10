package net.server.channel.packet

import java.awt.Point

import net.server.MaplePacket

class ItemPickupPacket(private var _timestamp: Int, private var _characterPosition: Point, private var _objectId: Int) extends MaplePacket {
  def timestamp: Int = _timestamp

  def characterPosition: Point = _characterPosition

  def objectId: Int = _objectId
}
