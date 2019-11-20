package net.server.channel.packet.bbs

class DeleteReplyPacket(private var _mode: Byte, private var _replyId: Int) extends BaseBBSOperationPacket(_mode = _mode) {
  def replyId: Int = _replyId
}
