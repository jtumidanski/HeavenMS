package net.server.channel.packet

import net.server.MaplePacket

class MobDamageMobFriendlyPacket( private var _attacker: Int,  private var _damage: Int) extends MaplePacket {
     def attacker: Int = _attacker
     def damage: Int = _damage
}
