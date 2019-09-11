package net.server.channel.packet.report

import net.server.MaplePacket

class BaseReportPacket( private var _type: Int,  private var _victim: String,  private var _reason: Int,  private var _description: String) extends MaplePacket {
     def theType: Int = _type
     def victim: String = _victim
     def reason: Int = _reason
     def description: String = _description
}
