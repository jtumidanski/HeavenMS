package net.server.channel.packet.mts

class CancelWantedCartPacket(private var _available: Boolean, private var _operation: Byte) extends BaseMTSPacket(_available = _available, _operation = _operation) {
}
