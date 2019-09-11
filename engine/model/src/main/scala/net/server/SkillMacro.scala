package net.server

class SkillMacro(private var _name: String, private var _shout: Int, private var _skill1: Int, private var _skill2: Int, private var _skill3: Int, private var _position: Int) {
  def name: String = _name

  def shout: Int = _shout

  def skill1: Int = _skill1

  def skill2: Int = _skill2

  def skill3: Int = _skill3

  def position: Int = _position
}
