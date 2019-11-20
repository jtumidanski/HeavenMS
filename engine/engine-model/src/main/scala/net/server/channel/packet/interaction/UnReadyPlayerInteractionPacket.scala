package net.server.channel.packet.interaction

class UnReadyPlayerInteractionPacket(private var _mode: Byte) extends BasePlayerInteractionPacket(_mode = _mode) {
}