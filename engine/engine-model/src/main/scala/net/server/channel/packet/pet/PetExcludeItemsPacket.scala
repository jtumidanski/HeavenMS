package net.server.channel.packet.pet

import net.server.MaplePacket

class PetExcludeItemsPacket( private var _petId: Int,  private var _amount: Byte,  private var _itemIds: Array[Int]) extends MaplePacket {
     def petId: Int = _petId
     def amount: Byte = _amount
     def itemIds: Array[Int] = _itemIds
}
