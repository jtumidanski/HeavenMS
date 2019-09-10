package net.server.channel.packet.interaction

class ExitAfterGamePlayerInteractionPacket(private var _mode: Byte) extends BasePlayerInteractionPacket(_mode = _mode) {
}