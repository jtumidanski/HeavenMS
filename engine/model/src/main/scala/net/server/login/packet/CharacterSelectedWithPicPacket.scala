package net.server.login.packet

import net.server.MaplePacket

class CharacterSelectedWithPicPacket(private var _pic: String, private var _characterId: Int, private var _macs: String, private var _hwid: String) extends MaplePacket {
  def pic: String = _pic

  def characterId: Int = _characterId

  def macs: String = _macs

  def hwid: String = _hwid
}
