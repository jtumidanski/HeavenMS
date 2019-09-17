package net.server.channel.packet.special

import java.awt.Point

class MonsterMagnetPacket(private var _skillId: Int, private var _skillLevel: Int, private var _position: Point, private var _monsterData: Array[MonsterMagnetData], private var _direction: Byte) extends BaseSpecialMovePacket(_skillId = _skillId, _skillLevel = _skillLevel, _position = _position) {
  def monsterData: Array[MonsterMagnetData] = _monsterData

  def direction: Byte = _direction
}
