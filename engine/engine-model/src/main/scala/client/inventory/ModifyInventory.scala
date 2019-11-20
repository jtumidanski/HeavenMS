package client.inventory

class ModifyInventory(private var _mode: Int, private var _item: Item, private var _oldPos: Short) {
  def mode: Int = _mode

  def item: Item = _item

  def oldPos: Short = _oldPos

  def this(mode: Int, item: Item) = {
    this(mode, item, 0)
  }

  def clear(): Unit = {
    _item = null
  }

  def position(): Short = {
    item.position
  }

  def inventoryType(): Int = {
    item.inventoryType.getType
  }

  def quantity(): Short = {
    item.quantity
  }
}
