package net.server.channel.packet.command.admin

import net.server.MaplePacket

class EnteringMapPacket( private var _mode: Byte,  private var _type: Byte) extends BaseAdminCommandPacket(_mode = _mode) {
     def theType: Byte = _type
}
