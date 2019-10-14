package server.maps

import java.awt.Point

class MapleDragon(private var _ownerId: Int, _position: Point, _stance: Int) extends AbstractAnimatedMapleMapObject {
  position_$eq(_position)
  stance_$eq(_stance)

  def ownerId: Int = _ownerId

  override def `type`(): MapleMapObjectType = MapleMapObjectType.DRAGON

  override def objectId: Int = _ownerId
}
