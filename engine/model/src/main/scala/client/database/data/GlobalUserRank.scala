package client.database.data

class GlobalUserRank(private var _name: String, private var _level: Int) {
  def name: String = _name

  def level: Int = _level
}
