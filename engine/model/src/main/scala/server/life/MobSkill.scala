package server.life

import java.awt.Point

class MobSkill(private var _skillId: Int, private var _level: Int, private var _summons: java.util.List[java.lang.Integer],
               private var _coolTime: Long, private var _duration: Long, private var _hp: Int, private var _mpCon: Int,
               private var _spawnEffect: Int, private var _x: Int, private var _y: Int, private var _prop: Float,
               private var _limit: Int, private var _lt: Point, private var _rb: Point) {
  def x: Int = _x

  def y: Int = _y

  def limit: Int = _limit

  def duration: Long = _duration

  def spawnEffect: Int = _spawnEffect

  def mpCon: Int = _mpCon

  def hp: Int = _hp

  def coolTime: Long = _coolTime

  def skillId: Int = _skillId

  def level: Int = _level

  def lt: Option[Point] = Option.apply(_lt)

  def rb: Option[Point] = Option.apply(_rb)

  def summons: java.util.List[java.lang.Integer] = _summons

  def makeChanceResult(): Boolean = _prop == 1.0 || Math.random() < _prop
}
