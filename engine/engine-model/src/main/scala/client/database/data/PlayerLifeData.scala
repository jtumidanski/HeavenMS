package client.database.data

class PlayerLifeData(private var _lifeId: Int, private var _type: String, private var _cy: Int, private var _f: Int,
                     private var _fh: Int, private var _rx0: Int, private var _rx1: Int, private var _x: Int,
                     private var _y: Int, private var _hide: Int, private var _mobTime: Int, private var _team: Int) {
  def lifeId: Int = _lifeId

  def theType: String = _type

  def cy: Int = _cy

  def f: Int = _f

  def fh: Int = _fh

  def rx0: Int = _rx0

  def rx1: Int = _rx1

  def x: Int = _x

  def y: Int = _y

  def hide: Int = _hide

  def mobTime: Int = _mobTime

  def team: Int = _team
}
