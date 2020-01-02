package net.server.coordinator.matchchecker

class MapleMatchCheckingEntry(private var _cid: Int) {
  def cid: Int = _cid

  private var _accepted: Boolean = false

  def accepted: Boolean = _accepted

  def accept(): Boolean = {
    if (!_accepted) {
      _accepted = true
      true
    } else {
      false
    }
  }
}
