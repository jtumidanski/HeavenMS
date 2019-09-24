package server.maps

class SavedLocation(private var _mapId: Int, private var _portal: Int) {
  def mapId: Int = _mapId

  def portal: Int = _portal
}
