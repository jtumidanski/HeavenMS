package net.server.channel.packet.interaction

class CancelExitAfterGamePlayerInteractionPacket(private var _mode: Byte) extends BasePlayerInteractionPacket(_mode = _mode) {
}