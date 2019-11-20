package client.database.data

import java.sql.Timestamp
import java.util.Date

class FrederickStorageData(private var _characterId: Integer, private var _name: String, private var _worldId: Integer,
                           private var _timestamp: Date, private var _dayNotes: Integer,
                           private var _lastLogoutTime: Date) {
  def characterId: Int = _characterId

  def name: String = _name

  def worldId: Int = _worldId

  def timestamp: Timestamp = _timestamp.asInstanceOf[Timestamp]

  def dayNotes: Int = _dayNotes

  def lastLogoutTime: Timestamp = _lastLogoutTime.asInstanceOf[Timestamp]
}
