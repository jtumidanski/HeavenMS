package server.movement

import java.awt.Point

abstract class AbstractLifeMovement(private var _type: Int, private var _position: Point, private var _duration: Int, private var _newState: Int) extends LifeMovement {
  def theType: Int = _type

  def position: Point = _position

  def duration: Int = _duration

  def newState: Int = _newState
}
