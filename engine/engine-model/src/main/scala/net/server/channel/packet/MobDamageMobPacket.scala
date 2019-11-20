package net.server.channel.packet

import net.server.MaplePacket

class MobDamageMobPacket( private var _from: Int,  private var _to: Int,  private var _magic: Boolean,  private var _damage: Int) extends MaplePacket {
     def from: Int = _from
     def to: Int = _to
     def magic: Boolean = _magic
     def damage: Int = _damage
}
