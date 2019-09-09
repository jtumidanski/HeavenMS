package net.server.login.packet

import net.server.MaplePacket

class CharacterListRequestPacket(private var _world: Int, private var _channel: Int) extends MaplePacket {
  def world: Int = _world

  def channel: Int = _channel
}
