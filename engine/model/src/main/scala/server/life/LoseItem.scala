package server.life

class LoseItem(private var _id: Int, private var _chance: Byte, private var _x: Byte) {
  def id: Int = _id

  def chance: Byte = _chance

  def x: Byte = _x
}
