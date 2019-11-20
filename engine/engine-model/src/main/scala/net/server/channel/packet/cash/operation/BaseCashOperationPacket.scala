package net.server.channel.packet.cash.operation

import net.server.MaplePacket

class BaseCashOperationPacket( private var _action: Int) extends MaplePacket {
     def action: Int = _action
}
