package net.server.channel.packet.pet

import net.server.MaplePacket

class PetLootPacket( private var _petIndex: Int,  private var _objectId: Int) extends MaplePacket {
     def petIndex: Int = _petIndex
     def objectId: Int = _objectId
}
