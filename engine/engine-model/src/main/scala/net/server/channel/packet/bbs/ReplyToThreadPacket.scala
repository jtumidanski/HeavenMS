package net.server.channel.packet.bbs

class ReplyToThreadPacket(private var _mode: Byte, private var _threadId: Int, private var _message: String) extends BaseBBSOperationPacket(_mode = _mode) {
  def threadId: Int = _threadId

  def message: String = _message
}
