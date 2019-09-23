package client.database.data

import java.sql.Timestamp

class FrederickStorageData(private var _characterId: Int, private var _name: String, private var _worldId: Int,
                           private var _timestamp: Timestamp, private var _dayNotes: Int,
                           private var _lastLogoutTime: Timestamp) {
  def characterId: Int = _characterId

  def name: String = _name

  def worldId: Int = _worldId

  def timestamp: Timestamp = _timestamp

  def dayNotes: Int = _dayNotes

  def lastLogoutTime: Timestamp = _lastLogoutTime
}
