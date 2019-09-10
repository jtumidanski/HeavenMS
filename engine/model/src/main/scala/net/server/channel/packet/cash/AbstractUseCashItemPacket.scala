package net.server.channel.packet.cash

import net.server.MaplePacket

abstract class AbstractUseCashItemPacket(private var _position: Short, private var _itemId: Int) extends MaplePacket {
  def position: Short = _position

  def itemId: Int = _itemId
}
