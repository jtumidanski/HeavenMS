package net.server.login.packet

import net.server.MaplePacket

class CheckCharacterNamePacket(private var _name: String) extends MaplePacket {
  def name: String = _name
}
