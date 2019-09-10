package net.server.channel.packet.guild

import net.server.MaplePacket

class BaseGuildOperationPacket( private var _type: Byte) extends MaplePacket {
     def theType: Byte = _type
}
