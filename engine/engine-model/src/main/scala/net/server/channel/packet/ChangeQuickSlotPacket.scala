package net.server.channel.packet

import net.server.MaplePacket

class ChangeQuickSlotPacket(private var _keyMap: Array[Byte]) extends MaplePacket {
  def keyMap: Array[Byte] = _keyMap
}
