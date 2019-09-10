package net.server.channel.packet

import net.server.MaplePacket

class HealOvertimePacket( private var _healHP: Short,  private var _healMP: Short) extends MaplePacket {
     def healHP: Short = _healHP
     def healMP: Short = _healMP
}
