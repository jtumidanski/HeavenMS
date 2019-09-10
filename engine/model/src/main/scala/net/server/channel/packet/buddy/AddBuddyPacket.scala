package net.server.channel.packet.buddy

class AddBuddyPacket(private var _mode: Int, private var _name: String, private var _group: String) extends BaseBuddyPacket(_mode = _mode) {
  def name: String = _name

  def group: String = _group
}
