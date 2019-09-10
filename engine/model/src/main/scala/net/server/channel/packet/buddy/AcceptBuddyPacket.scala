package net.server.channel.packet.buddy

class AcceptBuddyPacket(private var _mode: Int, private var _otherCharacterId: Int) extends BaseBuddyPacket(_mode = _mode) {
  def otherCharacterId: Int = _otherCharacterId
}
