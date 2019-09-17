package net.server.channel.packet.command.admin

class BanPlayerPacket(private var _mode: Byte) extends BaseAdminCommandPacket(_mode = _mode) {
}
