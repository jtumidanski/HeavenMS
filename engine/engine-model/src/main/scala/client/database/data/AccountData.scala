package client.database.data

class AccountData(private var _id: Integer, private var _name: String, private var _password: String,
                  private var _gender: Integer, private var _banned: Boolean, private var _pin: String,
                  private var _pic: String, private var _characterSlots: Integer, private var _tos: Boolean,
                  private var _language: String, private var _country: String) {
  def id: Int = _id

  def name: String = _name

  def password: String = _password

  def gender: Integer = _gender

  def banned: Boolean = _banned

  def pin: String = _pin

  def pic: String = _pic

  def characterSlots: Integer = _characterSlots

  def tos: Boolean = _tos

  def language: String = _language

  def country: String = _country
}
