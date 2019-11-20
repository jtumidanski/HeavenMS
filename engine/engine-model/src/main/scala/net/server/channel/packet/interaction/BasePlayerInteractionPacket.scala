package net.server.channel.packet.interaction

import net.server.MaplePacket

class BasePlayerInteractionPacket(private var _mode: Byte) extends MaplePacket {
  def mode: Byte = _mode
}
