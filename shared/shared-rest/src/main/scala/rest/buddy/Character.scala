package rest.buddy

class Character() {
  private var id: Integer = _

  def getId: Integer = id

  def setId(value: Integer): Unit = id = value

  private var accountId: Integer = _

  def getAccountId: Integer = accountId

  def setAccountId(value: Integer): Unit = accountId = value

  private var capacity: Integer = _

  def getCapacity(): Integer = capacity

  def setCapacity(value: Integer): Unit = capacity = value

  def this(_id: Integer, _accountId: Integer, _capacity: Integer) = {
    this()
    id = _id
    accountId = _accountId
    capacity = _capacity
  }
}
