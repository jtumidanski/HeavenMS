package net.server.channel.packet.command.admin

import net.server.MaplePacket

class PlayerWarnPacket( private var _mode: Byte,  private var _victim: String,  private var _message: String) extends BaseAdminCommandPacket(_mode = _mode) {
     def victim: String = _victim
     def message: String = _message
}
