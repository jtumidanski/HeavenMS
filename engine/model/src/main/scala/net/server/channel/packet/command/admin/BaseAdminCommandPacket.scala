package net.server.channel.packet.command.admin

import net.server.MaplePacket

class BaseAdminCommandPacket(private var _mode: Byte) extends MaplePacket {
     def mode: Byte = _mode
}
