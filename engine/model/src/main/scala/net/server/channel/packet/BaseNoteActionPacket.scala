package net.server.channel.packet

import net.server.MaplePacket

class BaseNoteActionPacket( private var _action: Int) extends MaplePacket {
     def action: Int = _action
}
