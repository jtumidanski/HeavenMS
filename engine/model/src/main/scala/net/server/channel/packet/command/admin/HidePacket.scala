package net.server.channel.packet.command.admin

import net.server.MaplePacket

class HidePacket( private var _mode: Byte,  private var _hide: Boolean) extends BaseAdminCommandPacket(_mode = _mode) {
     def hide: Boolean = _hide
}
