package net.server.channel.packet

import net.server.MaplePacket

class WhisperPacket(private var _mode: Byte, private var _recipient: String, private var _message: String) extends MaplePacket {
  def mode: Byte = _mode

  def recipient: String = _recipient

  def message: String = _message
}
