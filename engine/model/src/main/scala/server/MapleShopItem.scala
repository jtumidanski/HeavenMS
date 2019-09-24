package server

class MapleShopItem(private var _buyable: Short, private var _itemId: Int, private var _price: Int, private var _pitch: Int) {
  def buyable: Short = _buyable

  def itemId: Int = _itemId

  def price: Int = _price

  def pitch: Int = _pitch
}
