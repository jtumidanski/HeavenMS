package net.server.channel.packet

import net.server.MaplePacket

class SpawnPetPacket( private var _slot: Byte,  private var _lead: Boolean) extends MaplePacket {
     def slot: Byte = _slot
     def lead: Boolean = _lead
}
