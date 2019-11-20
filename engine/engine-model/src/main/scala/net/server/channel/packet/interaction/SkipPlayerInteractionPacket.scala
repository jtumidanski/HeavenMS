package net.server.channel.packet.interaction

class SkipPlayerInteractionPacket(private var _mode: Byte) extends BasePlayerInteractionPacket(_mode = _mode) {
}