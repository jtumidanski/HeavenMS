package net.server.channel.packet

import net.server.MaplePacket

abstract class BaseTeleportRockMapPacket(private var _type: Byte, private var _vip: Boolean) extends MaplePacket {
  def theType: Byte = _type

  def vip: Boolean = _vip
}
