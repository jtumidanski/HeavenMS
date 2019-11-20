package rest.master

class Character() {
  private var id: Integer = _

  def getId: Integer = id

  def setId(value: Integer): Unit = id = value

  private var accountId: Integer = _

  def getAccountId: Integer = accountId

  def setAccountId(value: Integer): Unit = accountId = value

  def this(_id: Integer, _accountId: Integer) = {
    this()
    id = _id
    accountId = _accountId
  }
}
