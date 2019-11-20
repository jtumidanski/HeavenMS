package server.maps

import java.awt.Point

abstract class AbstractMapleMapObject() extends MapleMapObject {
  private var _position: Option[Point] = Option.apply(new Point())

  private var _objectId: Int = 0

  override def objectId: Int = _objectId

  override def objectId_$eq(value: Int): Unit = _objectId = value

  def position: Point = {
    new Point(_position.get)
  }

  override def position_$eq(position: Point): Unit = {
    if (_position.isDefined) {
      _position.get.x = position.x
      _position.get.y = position.y
    }
  }

  override def nullifyPosition(): Unit = _position = Option.empty
}
