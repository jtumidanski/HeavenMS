package net.server.login.packet

import net.server.MaplePacket

class AfterLoginPacket(private var _byte1: Byte, private var _byte2: Byte, private var _pin: String) extends MaplePacket {
  def byte1: Byte = _byte1

  def byte2: Byte = _byte2

  def pin: String = _pin

  def this(_byte1: Byte) {
    this(_byte1, 5, "")
  }

  def this(_byte1: Byte, _byte2: Byte) {
    this(_byte1, _byte2, "")
  }
}
