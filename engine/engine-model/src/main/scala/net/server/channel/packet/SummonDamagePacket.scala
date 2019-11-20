package net.server.channel.packet

import net.server.MaplePacket

class SummonDamagePacket(private var _objectId: Int, private var _direction: Byte, private var _numAttacked: Int, private var _monsterObjectId: Array[Int], private var _damage: Array[Int]) extends MaplePacket {
  def objectId: Int = _objectId

  def direction: Byte = _direction

  def numAttacked: Int = _numAttacked

  def monsterObjectId: Array[Int] = _monsterObjectId

  def damage: Array[Int] = _damage
}
