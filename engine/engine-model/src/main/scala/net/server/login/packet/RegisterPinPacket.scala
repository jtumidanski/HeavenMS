package net.server.login.packet

import net.server.MaplePacket

class RegisterPinPacket(private var _byte1: Byte, private var _pin: String) extends MaplePacket {
  def byte1: Byte = _byte1

  def pin: String = _pin
}
