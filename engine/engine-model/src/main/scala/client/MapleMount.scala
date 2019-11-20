package client

class MapleMount(var itemId: Int, var skillId: Int) {
  private var _tiredness: Int = 0

  var level: Int = 1

  var exp: Int = 0

  var active: Boolean = true

  /**
   * 1902000 - Hog
   * 1902001 - Silver Mane
   * 1902002 - Red Draco
   * 1902005 - Mimiana
   * 1902006 - Mimio
   * 1902007 - Shinjou
   * 1902008 - Frog
   * 1902009 - Ostrich
   * 1902010 - Frog
   * 1902011 - Turtle
   * 1902012 - Yeti
   */
  def id: Int = {
    if (itemId < 1903000) return itemId - 1901999
    5
  }

  def id_=(newId: Int): Unit = {
    itemId = newId
  }

  def tiredness: Int = _tiredness

  def tiredness_=(newTiredness: Int): Unit = {
    _tiredness = newTiredness
    if (newTiredness < 0) {
      _tiredness = 0
    }
  }

  def incrementAndGetTiredness(): Int = {
    _tiredness += 1
    _tiredness
  }
}
