package net.server.channel.packet

import net.server.MaplePacket

class ChangeMapPacket(private var _cashShop: Boolean, private var _fromDying: Byte, private var _targetId: Int, private var _startWarp: String, private var _wheel: Boolean) extends MaplePacket {
  def cashShop: Boolean = _cashShop

  def fromDying: Byte = _fromDying

  def targetId: Int = _targetId

  def startWarp: String = _startWarp

  def wheel: Boolean = _wheel
}
