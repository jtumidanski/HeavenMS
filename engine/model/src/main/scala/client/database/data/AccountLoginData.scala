package client.database.data

import java.sql.{Date, Timestamp}

class AccountLoginData(private var _loggedIn: Int, private var _lastLogin: Timestamp, private var _birthday: Date) {
  def loggedIn: Int = _loggedIn

  def lastLogin: Timestamp = _lastLogin

  def birthday: Date = _birthday
}
