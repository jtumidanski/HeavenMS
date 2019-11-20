package client.database.data

class PlayerNpcFieldData(private var _worldId: Int, private var _mapId: Int, private var _step: Int,
                         private var _podium: Int) {
  def worldId: Int = _worldId

  def mapId: Int = _mapId

  def step: Int = _step

  def podium: Int = _podium
}
