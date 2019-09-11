package net.server.channel.packet

import net.server.MaplePacket

class SpouseChatPacket( private var _recipient: String,  private var _message: String) extends MaplePacket {
     def recipient: String = _recipient
     def message: String = _message
}
