package net.server.channel.packet

import net.server.MaplePacket

class NPCMoreTalkPacket( private var _lastMessageType: Byte,  private var _action: Byte,  private var _returnText: String,  private var _selection: Int) extends MaplePacket {
     def lastMessageType: Byte = _lastMessageType
     def action: Byte = _action
     def returnText: String = _returnText
     def selection: Int = _selection
}
