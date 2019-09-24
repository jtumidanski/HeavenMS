package server.life

class SelfDestruction(private var _action: Byte, private var _removeAfter: Int, private var _hp: Int) {
  def action: Byte = _action

  def removeAfter: Int = _removeAfter

  def hp: Int = _hp
}
