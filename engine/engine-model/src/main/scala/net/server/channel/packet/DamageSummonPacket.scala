package net.server.channel.packet

import net.server.MaplePacket

class DamageSummonPacket(private var _objectId: Int, private var _damage: Int, private var _monsterIdFrom: Int) extends MaplePacket {
  def objectId: Int = _objectId

  def damage: Int = _damage

  def monsterIdFrom: Int = _monsterIdFrom
}
