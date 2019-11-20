package net.server.channel.packet.command.admin

import net.server.MaplePacket

class TestingPacket( private var _mode: Byte,  private var _printableInt: Int) extends BaseAdminCommandPacket(_mode = _mode) {
     def printableInt: Int = _printableInt
}
