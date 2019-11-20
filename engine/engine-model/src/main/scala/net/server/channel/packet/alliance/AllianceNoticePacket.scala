package net.server.channel.packet.alliance

class AllianceNoticePacket(private var _notice: String) extends AllianceOperationPacket {
  def notice: String = _notice
}
