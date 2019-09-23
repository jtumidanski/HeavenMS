package client.database.data

class CharacterRankData(private var _lastLogin: Long, private var _loggedIn: Int, private var _move: Int,
                        private var _rank: Int, private var _characterId: Int) {
  def lastLogin: Long = _lastLogin

  def loggedIn: Int = _loggedIn

  def move: Int = _move

  def rank: Int = _rank

  def characterId: Int = _characterId
}
