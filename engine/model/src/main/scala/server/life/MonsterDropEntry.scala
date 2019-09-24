package server.life

class MonsterDropEntry(private var _itemId: Int, private var _chance: Int, private var _minimum: Int,
                       private var _maximum: Int, private var _questId: Short) {
  def itemId: Int = _itemId

  def chance: Int = _chance

  def minimum: Int = _minimum

  def maximum: Int = _maximum

  def questId: Short = _questId
}
