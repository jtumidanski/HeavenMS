package net.server.channel.packet.interaction

class ReadyPlayerInteractionPacket(private var _mode: Byte) extends BasePlayerInteractionPacket(_mode = _mode) {
}