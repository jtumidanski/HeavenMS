package client.database.data

class SavedLocationData(private var _locationType: String, private var _mapId: Int, private var _portalId: Int) {
  def locationType: String = _locationType

  def mapId: Int = _mapId

  def portalId: Int = _portalId
}
