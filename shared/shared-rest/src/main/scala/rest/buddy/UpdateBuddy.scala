package rest.buddy

class UpdateBuddy {
  var pending: Integer = _

  def getPending(value: Integer): Unit = pending = value

  def this(_pending: Integer) = {
    this()
    pending = _pending
  }
}
