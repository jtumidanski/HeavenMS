package client.database.data

class CharacterIdNameAccountId(private var _id: Int, private var _accountId: Int, private var _name: String) {
  def id: Int = _id

  def accountId: Int = _accountId

  def name: String = _name
}
