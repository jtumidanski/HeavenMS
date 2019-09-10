package net.server.channel.packet

import net.server.MaplePacket

class MultiChatPacket(private var _type: Int, private var _recipients: Int, private var _recipientIds: Array[Int], private var _message: String) extends MaplePacket {
  def theType: Int = _type

  def recipients: Int = _recipients

  def recipientIds: Array[Int] = _recipientIds

  def message: String = _message
}
