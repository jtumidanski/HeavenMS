package net.server.channel.packet.mts

import net.server.MaplePacket

class BaseMTSPacket(private var _available: Boolean, private var _operation: Byte) extends MaplePacket {
  def available: Boolean = _available

  def operation: Byte = _operation
}
