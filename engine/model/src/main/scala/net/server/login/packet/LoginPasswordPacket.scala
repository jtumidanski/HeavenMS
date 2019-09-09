package net.server.login.packet

import net.server.MaplePacket

class LoginPasswordPacket(private var _login: String, private var _password: String, private var _hwid: Array[Byte]) extends MaplePacket {
  def login: String = _login

  def password: String = _password

  def hwid: Array[Byte] = _hwid
}
