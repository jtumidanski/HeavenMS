package client

class BuddyListEntry(private var _name: String, var group: String, private var _characterId: Int, var channel: Int, private var _visible: Boolean) {
  def name: String = _name

  def characterId: Int = _characterId

  def visible: Boolean = _visible

  override def hashCode(): Int = {
    val prime: Int = 31
    var result: Int = 1
    result = prime * result + _characterId
    result
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      return true
    }
    if (obj == null) {
      return false
    }
    if (getClass != obj.getClass) {
      return false
    }
    val other: BuddyListEntry = obj.asInstanceOf[BuddyListEntry]
    _characterId == other.characterId
  }
}
