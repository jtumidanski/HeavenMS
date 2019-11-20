package rest.master

class CharactersResponse(private var _characters: java.util.List[Character]) {
  def getCharacters(): java.util.List[Character] = _characters

  def setCharacters(value: java.util.List[Character]): Unit = _characters = value

  def this() = {
    this(null)
  }
}
