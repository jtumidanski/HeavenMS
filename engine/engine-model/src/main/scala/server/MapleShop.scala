package server

class MapleShop(private var _id: Int, private var _npcId: Int) {
  def id: Int = _id

  def npcId: Int = _npcId

  private var _items: Array[MapleShopItem] = new Array[MapleShopItem](0)

  def setItems(items: Array[MapleShopItem]):Unit = _items = items

  def items: Array[MapleShopItem] = _items

  def findBySlot(slot: Short): Option[MapleShopItem] = {
    if (slot < 0 || slot >= _items.length) {
      Option.empty
    }
    Option.apply(_items(slot))
  }

  def tokenValue: Int = 1000000000

  def token: Int = 4000313
}
