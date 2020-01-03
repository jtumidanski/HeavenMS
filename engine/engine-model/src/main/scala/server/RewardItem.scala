package server

class RewardItem(private var _itemId: Int, private var _period: Int, private var _probability: Short,
                 private var _quantity: Short, private var _effect: String, private var _worldMessage: String) {
  def itemId: Int = _itemId

  def period: Int = _period

  def probability: Short = _probability

  def quantity: Short = _quantity

  def effect: String = _effect

  def worldMessage: String = _worldMessage
}
