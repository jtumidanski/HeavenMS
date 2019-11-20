package net.server

import java.awt.Point

class AbsoluteMovementData(private var _position: Point, private var _stance: Byte) extends MovementData {
  def position: Point = _position

  def stance: Byte = _stance
}
