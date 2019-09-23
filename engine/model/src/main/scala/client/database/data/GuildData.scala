package client.database.data

class GuildData(private var _name: String, private var _gp: Int, private var _logo: Int, private var _logoColor: Int,
                private var _logoBackground: Int, private var _logoBackgroundColor: Int, private var _capacity: Int,
                private var _rankTitles: Array[String], private var _leaderId: Int, private var _notice: String,
                private var _signature: Int, private var _allianceId: Int) {
  def name: String = _name

  def gp: Int = _gp

  def logo: Int = _logo

  def logoColor: Int = _logoColor

  def logoBackground: Int = _logoBackground

  def logoBackgroundColor: Int = _logoBackgroundColor

  def capacity: Int = _capacity

  def rankTitles: Array[String] = _rankTitles

  def leaderId: Int = _leaderId

  def notice: String = _notice

  def signature: Int = _signature

  def allianceId: Int = _allianceId

  def this(name: String, gp: Int, logo: Int, logoColor: Int, logoBackground: Int, logoBackgroundColor: Int) = {
    this(name, gp, logo, logoColor, logoBackground, logoBackgroundColor, 0, Array[String](), 0, "", 0, 0)
  }
}
