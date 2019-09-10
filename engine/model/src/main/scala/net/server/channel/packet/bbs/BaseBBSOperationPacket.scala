package net.server.channel.packet.bbs

import net.server.MaplePacket

class BaseBBSOperationPacket( private var _mode: Byte) extends MaplePacket {
     def mode: Byte = _mode
}
