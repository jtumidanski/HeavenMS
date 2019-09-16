package net.server.channel.packet

import net.server.MaplePacket

class AdminChatPacket( private var _mode: Byte,  private var _message: String,  private var _noticeType: Int) extends MaplePacket {
     def mode: Byte = _mode
     def message: String = _message
     def noticeType: Int = _noticeType
}
