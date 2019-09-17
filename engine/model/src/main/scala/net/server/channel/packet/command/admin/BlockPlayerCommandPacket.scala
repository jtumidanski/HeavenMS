package net.server.channel.packet.command.admin

class BlockPlayerCommandPacket(private var _mode: Byte, private var _victim: String, private var _type: Int, private var _duration: Int, private var _description: String) extends BaseAdminCommandPacket(_mode = _mode) {
  def victim: String = _victim

  def theType: Int = _type

  def duration: Int = _duration

  def description: String = _description
}
