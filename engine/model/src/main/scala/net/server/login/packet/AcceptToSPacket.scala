package net.server.login.packet

import net.server.MaplePacket

class AcceptToSPacket(private var _bytes: Array[Byte]) extends MaplePacket {
  def bytes: Array[Byte] = _bytes
}
