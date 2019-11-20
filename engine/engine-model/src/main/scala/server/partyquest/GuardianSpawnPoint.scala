package server.partyquest

import java.awt.Point

class GuardianSpawnPoint(private var _position: Point) {
  var taken: Boolean = true

  var team: Int = -1

  def position: Point = _position
}
