package net.server.channel.packet

import net.server.MaplePacket

class ItemMovePacket(private var _inventoryType: Byte, private var _source: Short, private var _action: Short, private var _quantity: Short) extends MaplePacket {
  def inventoryType: Byte = _inventoryType

  def source: Short = _source

  def action: Short = _action

  def quantity: Short = _quantity
}
