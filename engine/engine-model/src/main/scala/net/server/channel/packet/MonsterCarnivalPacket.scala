package net.server.channel.packet

import net.server.MaplePacket

class MonsterCarnivalPacket( private var _tab: Int,  private var _num: Int) extends MaplePacket {
     def tab: Int = _tab
     def num: Int = _num
}
