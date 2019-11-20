package net.server.channel.packet.command.admin

import net.server.MaplePacket

class MonsterHpPacket( private var _mode: Byte,  private var _mobHp: Int) extends BaseAdminCommandPacket(_mode = _mode) {
     def mobHp: Int = _mobHp
}
