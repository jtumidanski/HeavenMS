package net.server.channel.packet

import net.server.MaplePacket

class SkillEffectPacket(private var _skillId: Int, private var _level: Int, private var _flags: Byte, private var _speed: Int, private var _aids: Byte) extends MaplePacket {
  def skillId: Int = _skillId

  def level: Int = _level

  def flags: Byte = _flags

  def speed: Int = _speed

  def aids: Byte = _aids
}
