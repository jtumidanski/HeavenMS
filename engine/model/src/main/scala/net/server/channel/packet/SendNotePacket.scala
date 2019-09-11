package net.server.channel.packet

class SendNotePacket(private var _action: Int, private var _characterName: String, private var _message: String) extends BaseNoteActionPacket(_action = _action) {
  def characterName: String = _characterName

  def message: String = _message
}
