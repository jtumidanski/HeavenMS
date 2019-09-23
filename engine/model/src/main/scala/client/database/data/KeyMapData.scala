package client.database.data

class KeyMapData(private var _key: Int, private var _type: Int, private var _action: Int) {
  def key: Int = _key

  def theType: Int = _type

  def action: Int = _action
}
