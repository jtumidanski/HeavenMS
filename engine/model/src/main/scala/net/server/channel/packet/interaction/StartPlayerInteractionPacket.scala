package net.server.channel.packet.interaction

class StartPlayerInteractionPacket(private var _mode: Byte) extends BasePlayerInteractionPacket(_mode = _mode) {
}