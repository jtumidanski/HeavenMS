package client.database.data

class NoteData(private var _id: Int, private var _from: String, private var _message: String,
               private var _timestamp: Long, private var _fame: Int) {
  def id: Int = _id

  def from: String = _from

  def message: String = _message

  def timestamp: Long = _timestamp

  def fame: Int = _fame
}
