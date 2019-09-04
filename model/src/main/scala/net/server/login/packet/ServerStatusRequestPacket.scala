package net.server.login.packet

import net.server.MaplePacket

class ServerStatusRequestPacket(private var _world: Byte) extends MaplePacket {
  def world: Byte = _world
}
