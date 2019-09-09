package net.server.channel.packet

class UseTeleportRockPacket(private var _position: Short, private var _itemId: Int, private var _vip: Boolean, private var _mapId: Int, private var _name: String) extends AbstractUseCashItemPacket(_position, _itemId) {
  def vip: Boolean = _vip

  def mapId: Int = _mapId

  def name: String = _name
}
