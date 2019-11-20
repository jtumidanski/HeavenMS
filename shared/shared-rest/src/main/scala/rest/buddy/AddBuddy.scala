package rest.buddy

class AddBuddy() {
  var referenceCharacterId: Integer = _

  def setReferenceCharacterId(value: Integer): Unit = referenceCharacterId = value

  var addId: Integer = _

  def setAddId(value: Integer): Unit = addId = value

  var addName: String = _

  def setAddName(value: String): Unit = addName = value

  var group: String = _

  def setGroup(value: String): Unit = group = value

  def this(_referenceCharacterId: Integer, _addId: Integer, _addName: String, _group: String) = {
    this()
    referenceCharacterId = _referenceCharacterId
    addId = _addId
    addName = _addName
    group = _group
  }
}
