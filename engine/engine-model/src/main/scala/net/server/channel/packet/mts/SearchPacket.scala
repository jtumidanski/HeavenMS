package net.server.channel.packet.mts

class SearchPacket(private var _available: Boolean, private var _operation: Byte, private var _tab: Int, private var _type: Int, private var _ci: Int, private var _search: String) extends BaseMTSPacket(_available = _available, _operation = _operation) {
  def tab: Int = _tab

  def theType: Int = _type

  def ci: Int = _ci

  def search: String = _search
}
