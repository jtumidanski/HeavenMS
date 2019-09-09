package net.server.channel.packet

import net.server.MaplePacket

class GeneralChatPacket( private var _message: String,  private var _show: Int) extends MaplePacket {
     def message: String = _message
     def show: Int = _show
}
