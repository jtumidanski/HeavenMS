package net.server.channel.packet.interaction

class DeclinePlayerInteractionPacket(private var _mode: Byte) extends BasePlayerInteractionPacket(_mode = _mode) {
}