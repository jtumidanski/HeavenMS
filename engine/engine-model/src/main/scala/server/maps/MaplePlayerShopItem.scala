package server.maps

import client.inventory.Item

class MaplePlayerShopItem(private var _item: Item, var bundles: Short, private var _price: Int) {
  def item: Item = _item

  def price: Int = _price

  var doesExist: Boolean = true
}
