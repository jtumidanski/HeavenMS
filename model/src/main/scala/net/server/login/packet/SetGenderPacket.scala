package net.server.login.packet

import net.server.MaplePacket

class SetGenderPacket(private var _confirmed: Byte, private var _gender: Byte) extends MaplePacket {
  def confirmed: Byte = _confirmed

  def gender: Byte = _gender
}
