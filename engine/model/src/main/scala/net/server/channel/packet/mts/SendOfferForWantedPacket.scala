package net.server.channel.packet.mts

class SendOfferForWantedPacket(private var _available: Boolean, private var _operation: Byte) extends BaseMTSPacket(_available = _available, _operation = _operation) {
}