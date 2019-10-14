package server.maps

abstract class AbstractAnimatedMapleMapObject extends AbstractMapleMapObject with AnimatedMapleMapObject {
  var stance: Int = 0

  override def isFacingLeft: Boolean = Math.abs(stance) % 2 == 1
}
