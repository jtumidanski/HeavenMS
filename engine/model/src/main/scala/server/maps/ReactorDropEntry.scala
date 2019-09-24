package server.maps

class ReactorDropEntry(private var _itemId: Int, private var _chance: Int, private var _questId: Int) {
  def itemId: Int = _itemId

  def chance: Int = _chance

  def questId: Int = _questId
}
