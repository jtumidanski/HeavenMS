package rest.buddy

class AddCharacter {
  var characterId: Integer = _

  def setCharacterId(value: Integer): Unit = characterId = value

  var accountId: Integer = _

  def setAccountId(value: Integer): Unit = accountId = value

  def this(_characterId: Integer, _accountId: Integer) = {
    this()
    characterId = _characterId
    accountId = _accountId
  }
}
