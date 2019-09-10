package net.server.channel.packet.keymap

class AutoHPKeymapChangePacket(private var _available: Boolean, private var _mode: Int, private var _itemId: Int) extends BaseKeymapChangePacket(_available = _available, _mode = _mode) {
  def itemId: Int = _itemId
}
