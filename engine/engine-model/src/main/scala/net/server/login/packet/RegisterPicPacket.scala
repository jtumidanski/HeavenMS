package net.server.login.packet

import net.server.MaplePacket

class RegisterPicPacket(private var _characterId: Int, private var _macs: String, private var _hwid: String, private var _pic: String) extends MaplePacket {
  def characterId: Int = _characterId

  def macs: String = _macs

  def hwid: String = _hwid

  def pic: String = _pic
}
