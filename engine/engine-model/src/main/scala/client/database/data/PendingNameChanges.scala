package client.database.data

class PendingNameChanges(private var _id: Int, private var _characterId: Int, private var _oldName: String,
                         private var _newName: String) {
  def id: Int = _id

  def characterId: Int = _characterId

  def oldName: String = _oldName

  def newName: String = _newName
}
