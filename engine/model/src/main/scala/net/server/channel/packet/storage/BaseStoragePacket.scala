package net.server.channel.packet.storage

import net.server.MaplePacket

class BaseStoragePacket( private var _mode: Byte) extends MaplePacket {
     def mode: Byte = _mode
}
