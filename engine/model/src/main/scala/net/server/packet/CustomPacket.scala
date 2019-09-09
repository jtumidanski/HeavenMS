package net.server.packet

import net.server.MaplePacket

class CustomPacket(private var _bytes : Array[Byte]) extends MaplePacket {
  def bytes: Array[Byte] = _bytes
}
