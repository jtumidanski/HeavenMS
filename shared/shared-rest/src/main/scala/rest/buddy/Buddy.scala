package rest.buddy

class Buddy(private var _id: Integer, private var _group: String) {
  def getId: Integer = _id

  def getGroup: String = _group

  def this(id: Integer) = {
    this(id, null)
  }
}
