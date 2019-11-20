package net.server.channel.packet.wedding

import net.server.MaplePacket

class BaseWeddingTalkPacket( private var _action: Byte) extends MaplePacket {
     def action: Byte = _action
}
