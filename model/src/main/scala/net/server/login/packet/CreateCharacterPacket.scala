package net.server.login.packet

import net.server.MaplePacket

class CreateCharacterPacket(private var _name: String, private var _job: Int, private var _face: Int,
                            private var _hair: Int, private var _hairColor: Int, private var _skinColor: Int,
                            private var _top: Int, private var _bottom: Int, private var _shoes: Int,
                            private var _weapon: Int, private var _gender: Int) extends MaplePacket {
  def name: String = _name

  def job: Int = _job

  def face: Int = _face

  def hair: Int = _hair

  def hairColor: Int = _hairColor

  def skinColor: Int = _skinColor

  def top: Int = _top

  def bottom: Int = _bottom

  def shoes: Int = _shoes

  def weapon: Int = _weapon

  def gender: Int = _gender
}
