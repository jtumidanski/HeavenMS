package client.database.data

class GiftData(private var _sn: Int, private var _ringId: Int, private var _message: String,
               private var _from: String) {
  def sn: Int = _sn

  def ringId: Int = _ringId

  def message: String = _message

  def from: String = _from
}
