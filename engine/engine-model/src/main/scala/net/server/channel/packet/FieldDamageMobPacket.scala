package net.server.channel.packet

import net.server.MaplePacket

class FieldDamageMobPacket(private var _mobId: Int, private var _damage: Int) extends MaplePacket {
     def mobId: Int = _mobId
     def damage: Int = _damage
}
