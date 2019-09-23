package client.database.data

class AccountData(private var _id: Int, private var _name: String, private var _password: String,
                  private var _gender: Byte, private var _banned: Boolean, private var _pin: String,
                  private var _pic: String, private var _characterSlots: Byte, private var _tos: Byte,
                  private var _language: Int) {
  def id: Int = _id

  def name: String = _name

  def password: String = _password

  def gender: Byte = _gender

  def banned: Boolean = _banned

  def pin: String = _pin

  def pic: String = _pic

  def characterSlots: Byte = _characterSlots

  def tos: Byte = _tos

  def language: Int = _language
}
