package net.server.channel.packet.keymap

import net.server.MaplePacket

class BaseKeymapChangePacket(private var _available: Boolean, private var _mode: Int) extends MaplePacket {
  def available: Boolean = _available

  def mode: Int = _mode
}
