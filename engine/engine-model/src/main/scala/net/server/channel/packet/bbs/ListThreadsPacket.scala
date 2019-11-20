package net.server.channel.packet.bbs

class ListThreadsPacket(private var _mode: Byte, private var _start: Int) extends BaseBBSOperationPacket(_mode = _mode) {
  def start: Int = _start
}
