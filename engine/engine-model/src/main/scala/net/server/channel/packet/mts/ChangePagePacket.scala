package net.server.channel.packet.mts

class ChangePagePacket(private var _available: Boolean, private var _operation: Byte, private var _tab: Int, private var _type: Int, private var _page: Int) extends BaseMTSPacket(_available = _available, _operation = _operation) {
  def tab: Int = _tab

  def theType: Int = _type

  def page: Int = _page
}
