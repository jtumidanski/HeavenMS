package client.database.data

class CharacterNameNote(private var _characterId: Int, private var _characterName: String, private var _note: Int) {
  def characterId: Int = _characterId

  def characterName: String = _characterName

  def note: Int = _note
}
