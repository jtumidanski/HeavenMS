package client.database.data

import java.sql.Timestamp
import java.util.Date

class CharacterRankData(private var _lastLogin: Date, private var _loggedIn: Int, private var _move: Int,
                        private var _rank: Int, private var _characterId: Int) {
  def lastLogin: Timestamp = _lastLogin.asInstanceOf[Timestamp]

  def loggedIn: Int = _loggedIn

  def move: Int = _move

  def rank: Int = _rank

  def characterId: Int = _characterId
}
