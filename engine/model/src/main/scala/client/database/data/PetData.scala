package client.database.data

class PetData(private var _name: String, private var _level: Byte, private var _closeness: Int,
              private var _fullness: Int, private var _summoned: Boolean, private var _flag: Int) {
  def name: String = _name

  def level: Byte = _level

  def closeness: Int = _closeness

  def fullness: Int = _fullness

  def summoned: Boolean = _summoned

  def flag: Int = _flag
}
