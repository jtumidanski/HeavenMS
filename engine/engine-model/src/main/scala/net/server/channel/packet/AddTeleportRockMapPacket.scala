package net.server.channel.packet

class AddTeleportRockMapPacket(private var _type: Byte, private var _vip: Boolean) extends BaseTeleportRockMapPacket(_type = _type, _vip = _vip) {
}
