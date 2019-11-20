package rest.buddy

class UpdateCharacter {
  var capacity: Integer = _

  def setCapacity(value: Integer): Unit = capacity = value

  def this(_capacity: Integer) = {
    this()
    capacity = _capacity
  }
}
