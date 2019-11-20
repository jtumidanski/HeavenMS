package net.server.channel.packet

import net.server.MaplePacket

class MobBanishPlayerPacket( private var _mobId: Int) extends MaplePacket {
     def mobId: Int = _mobId
}
