package net.server.channel.packet.interaction

class CreateTradePlayerInteractionPacket(private var _mode: Byte, private var _createType: Byte) extends BaseCreatePlayerInteractionPacket(_mode: Byte, _createType: Byte) {
}
