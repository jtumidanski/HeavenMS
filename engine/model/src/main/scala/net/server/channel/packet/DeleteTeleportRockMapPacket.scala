package net.server.channel.packet

class DeleteTeleportRockMapPacket(private var _type: Byte, private var _vip: Boolean, private var _mapId: Int) extends BaseTeleportRockMapPacket(_type = _type, _vip = _vip) {
  def mapId: Int = _mapId
}
