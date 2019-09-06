package net.server.channel.packet

import net.server.MaplePacket

class NPCShopPacket(private var _mode: Byte, private var _slot: Short, private var _itemId: Int, private var _quantity: Short) extends MaplePacket {
  def mode: Byte = _mode

  def slot: Short = _slot

  def itemId: Int = _itemId

  def quantity: Short = _quantity
}
