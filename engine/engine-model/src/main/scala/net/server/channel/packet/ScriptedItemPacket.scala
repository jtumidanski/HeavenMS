package net.server.channel.packet

import net.server.MaplePacket

class ScriptedItemPacket( private var _timestamp: Int,  private var _itemSlot: Short,  private var _itemId: Int) extends MaplePacket {
     def timestamp: Int = _timestamp
     def itemSlot: Short = _itemSlot
     def itemId: Int = _itemId
}
