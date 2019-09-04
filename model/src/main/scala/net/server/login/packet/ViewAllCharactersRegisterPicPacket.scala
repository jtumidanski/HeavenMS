package net.server.login.packet

import net.server.MaplePacket

class ViewAllCharactersRegisterPicPacket(private var _characterId: Int, private var _mac: String, private var _hwid: String, private var _pic: String) extends MaplePacket {
  def characterId: Int = _characterId

  def mac: String = _mac

  def hwid: String = _hwid

  def pic: String = _pic
}
