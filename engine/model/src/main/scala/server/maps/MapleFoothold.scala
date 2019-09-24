package server.maps

import java.awt.Point

class MapleFoothold(private var _firstPoint: Point, private var _secondPoint: Point, private var _id: Int) extends Comparable[MapleFoothold] {
  def id: Int = _id

  def firstX: Int = _firstPoint.x

  def firstY: Int = _firstPoint.y

  def secondX: Int = _secondPoint.x

  def secondY: Int = _secondPoint.y

  def isWall: Boolean = _firstPoint.x == _secondPoint.x

  override def compareTo(o: MapleFoothold): Int = {
    if (secondY < o.firstY) {
      -1
    } else if (firstY > o.secondY) {
      1
    } else {
      0
    }
  }
}
