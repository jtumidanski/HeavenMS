package rest.master.world

class CharacterMessage(private var _message: String, private var _type: Int) {
  def message: String = _message

  def theType: Int = _type
}
