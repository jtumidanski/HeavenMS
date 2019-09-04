package net.server.login.packet

import net.server.MaplePacket

class DeleteCharacterPacket(private var _pic: String, private var _characterId: Int) extends MaplePacket {
  def pic: String = _pic

  def characterId: Int = _characterId
}
