package net.server.channel.packet.command.admin

import net.server.MaplePacket

class SummonMonsterPacket( private var _mode: Byte,  private var _mobId: Int,  private var _quantity: Int) extends BaseAdminCommandPacket(_mode = _mode) {
     def mobId: Int = _mobId
     def quantity: Int = _quantity
}
