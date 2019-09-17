package net.server.channel.packet.command.admin

import net.server.MaplePacket

class ChangeMapPacket( private var _mode: Byte,  private var _victim: String,  private var _mapId: Int) extends BaseAdminCommandPacket(_mode = _mode) {
     def victim: String = _victim
     def mapId: Int = _mapId
}
