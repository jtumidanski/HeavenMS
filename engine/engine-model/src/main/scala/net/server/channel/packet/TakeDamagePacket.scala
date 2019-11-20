package net.server.channel.packet

import net.server.MaplePacket

class TakeDamagePacket(private var _damageFrom: Byte, private var _element: Byte, private var _damage: Int, private var _monsterIdFrom: Int, private var _objectId: Int, private var _direction: Byte) extends MaplePacket {
  def damageFrom: Byte = _damageFrom

  def element: Byte = _element

  def damage: Int = _damage

  def monsterIdFrom: Int = _monsterIdFrom

  def objectId: Int = _objectId

  def direction: Byte = _direction
}
