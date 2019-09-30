package server.maps

class MaplePlayerShopSoldItem(private var _buyer: String, private var _itemId: Int, private var _quantity: Short,
                              private var _mesos: Int) {
  def buyer: String = _buyer

  def itemId: Int = _itemId

  def quantity: Short = _quantity

  def mesos: Int = _mesos
}
