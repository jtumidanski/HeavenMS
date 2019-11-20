package net.server.channel.packet.keymap

class RegularKeymapChangePacket(private var _available: Boolean, private var _mode: Int, private var _changes: Array[KeyTypeAction]) extends BaseKeymapChangePacket(_available = _available, _mode = _mode) {
  def changes: Array[KeyTypeAction] = _changes
}
