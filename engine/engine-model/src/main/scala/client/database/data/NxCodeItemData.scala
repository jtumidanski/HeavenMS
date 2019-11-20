package client.database.data

class NxCodeItemData(private var _type: Int, private var _quantity: Int, private var _itemId: Int) {
  def theType: Int = _type

  def quantity: Int = _quantity

  def itemId: Int = _itemId
}
