package server

class WorldRecommendation(private var _worldId: Int, private var _reason: String) {
  def worldId: Int = _worldId

  def reason: String = _reason

  override def toString: String = _worldId + ":" + _reason
}
