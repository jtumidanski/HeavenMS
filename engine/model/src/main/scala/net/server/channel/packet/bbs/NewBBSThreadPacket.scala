package net.server.channel.packet.bbs

class NewBBSThreadPacket(private var _mode: Byte, private var _isNotice: Boolean, private var _title: String, private var _text: String, private var _icon: Int) extends BaseBBSOperationPacket(_mode = _mode) {
  def isNotice: Boolean = _isNotice

  def title: String = _title

  def text: String = _text

  def icon: Int = _icon
}
