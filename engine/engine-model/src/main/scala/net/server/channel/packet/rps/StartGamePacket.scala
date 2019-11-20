package net.server.channel.packet.rps

class StartGamePacket(private var _available: Boolean, private var _mode: Byte) extends BaseRPSActionPacket(_available = _available, _mode = _mode) {
}
