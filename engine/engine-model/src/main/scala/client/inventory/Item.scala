package client.inventory

import java.util.concurrent.atomic.AtomicInteger

import constants.inventory.ItemConstants

class Item(var _id: Int, var _position: Short, var _quantity: Short, var _pet: Option[MaplePet], var _petId: Int) extends Comparable[Item] {
  def id: Int = _id

  def position: Short = _position

  def position_(position: Short): Unit = {
    _position = position
    if (_pet.nonEmpty) _pet.get.position_(position)
  }

  def petId: Int = _petId

  def pet: Option[MaplePet] = _pet

  protected var log: List[String] = List[String]()

  var flag: Short = 0

  var sn: Int = 0

  var giftFrom: String = ""

  var owner: String = ""

  protected var _expiration: Long = -1

  def quantity: Short = _quantity

  def quantity_=(quantity: Short): Unit = {
    _quantity = quantity
  }

  // pets & rings shares cashid values
  private var _runningCashId: AtomicInteger = new AtomicInteger(777000000)

  private var _cashId: Int = 0

  def cashId(): Int = {
    if (_cashId == 0) {
      _cashId = _runningCashId.getAndIncrement()
    }
    _cashId
  }

  def this(id: Int, position: Short, quantity: Short) = {
    this(id, position, quantity, Option.empty, -1)
  }

  def copy(): Item = {
    val ret: Item = new Item(_id, _position, quantity, _pet, _petId)
    ret.flag = flag
    ret.owner = owner
    ret._expiration = _expiration
    ret.log = log
    ret
  }

  override def compareTo(o: Item): Int = {
    if (this.id < o.id) {
      -1
    } else if (this.id > o.id) {
      1
    } else {
      0
    }
  }

  override def toString: String = "Item: " + _id + " quantity: " + quantity

  def expiration: Long = _expiration

  def expiration_(expiration: Long): Unit = {
    if (!ItemConstants.isPermanentItem(_id)) {
      _expiration = expiration
    } else {
      if (ItemConstants.isPet(_id)) {
        _expiration = Long.MaxValue
      } else {
        _expiration = -1
      }
    }
  }

  def itemType: Byte = {
    if (_petId > -1) {
      return 3
    }
    2
  }

  def inventoryType: MapleInventoryType = ItemConstants.getInventoryType(_id)
}
