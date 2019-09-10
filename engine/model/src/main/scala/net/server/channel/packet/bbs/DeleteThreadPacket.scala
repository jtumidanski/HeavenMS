package net.server.channel.packet.bbs

class DeleteThreadPacket(private var _mode: Byte, private var _threadId: Int) extends BaseBBSOperationPacket(_mode = _mode) {
  def threadId: Int = _threadId
}
