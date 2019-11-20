package net.server.channel.packet.command.admin

import net.server.MaplePacket

class SetPlayerExpPacket( private var _mode: Byte,  private var _amount: Int) extends BaseAdminCommandPacket(_mode = _mode) {
     def amount: Int = _amount
}
