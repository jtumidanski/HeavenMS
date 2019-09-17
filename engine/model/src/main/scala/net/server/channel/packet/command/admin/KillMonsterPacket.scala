package net.server.channel.packet.command.admin

import net.server.MaplePacket

class KillMonsterPacket( private var _mode: Byte,  private var _mobToKill: Int,  private var _amount: Int) extends BaseAdminCommandPacket(_mode = _mode) {
     def mobToKill: Int = _mobToKill
     def amount: Int = _amount
}
