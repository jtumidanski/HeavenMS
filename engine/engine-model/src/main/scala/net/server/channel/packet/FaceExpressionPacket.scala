package net.server.channel.packet

import net.server.MaplePacket

class FaceExpressionPacket( private var _emote: Int) extends MaplePacket {
     def emote: Int = _emote
}
