package server.maps

import java.awt.Point

class MapleKite(private var _ownerName: String, private var _pos: Point, private var _ft: Int, private var _text: String, private var _itemId: Int) extends AbstractMapleMapObject {
  def ownerName: String = _ownerName

  def pos: Point = _pos

  def ft: Int = _ft

  def text: String = _text

  def itemId: Int = _itemId

  override def `type`(): MapleMapObjectType = MapleMapObjectType.KITE

  override def position: Point = pos.getLocation

  override def position_$eq(position: Point): Unit = throw new UnsupportedOperationException
}
