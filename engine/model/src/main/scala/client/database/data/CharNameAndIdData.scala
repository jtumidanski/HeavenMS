package client.database.data

class CharNameAndIdData(private var _name: String, private var _id: Int, private var _buddyCapacity: Int) {
  def name: String = _name

  def id: Int = _id

  def buddyCapacity: Int = _buddyCapacity

  def this(name: String, id: Int) {
    this(name, id, 0)
  }
}
