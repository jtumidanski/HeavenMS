package client.database.data

import java.sql.Timestamp
import java.util.Date


class AccountLoginData(private var _loggedIn: Int, private var _lastLogin: Date, private var _birthday: Date) {
  def loggedIn: Int = _loggedIn

  def lastLogin: Timestamp = _lastLogin.asInstanceOf[Timestamp]

  def birthday: Date = _birthday
}
