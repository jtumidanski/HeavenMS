package net.server.channel.packet.rps

import net.server.MaplePacket

class BaseRPSActionPacket(private var _available: Boolean, private var _mode: Byte) extends MaplePacket {
  def available: Boolean = _available

  def mode: Byte = _mode
}
