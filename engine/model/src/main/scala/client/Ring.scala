package client

class Ring(private var _ringId: Int, private var _partnerRingId: Int, private var _partnerId: Int, private var _itemId: Int, private var _partnerName: String) extends Comparable[Ring] {
  def ringId: Int = _ringId

  def partnerRingId: Int = _partnerRingId

  def partnerId: Int = _partnerId

  def itemId: Int = _itemId

  def partnerName: String = _partnerName

  private var equipped: Boolean = false

  def isEquipped: Boolean = {
    equipped
  }

  def equip(): Unit = {
    equipped = true
  }

  def unequip(): Unit = {
    equipped = false
  }

  override def hashCode(): Int = {
    var hash: Int = 5
    hash = 53 * hash + _ringId
    hash
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case ring: Ring =>
        ring._ringId == _ringId
      case _ =>
        false
    }
  }

  override def compareTo(o: Ring): Int = {
    if (_ringId < o._ringId) {
      -1
    } else if (_ringId == o._ringId) {
      0
    } else {
      1
    }
  }
}
