package client.database.data

class CharacterWorldData(private var _characterId: Int, private var _worldId: Int) {
  def characterId: Int = _characterId

  def worldId: Int = _worldId
}
