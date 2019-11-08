package server.life

class MonsterGlobalDropEntry(private var _itemId: Int, private var _chance: Int, private var _continentId: Int,
                             private var _minimum: Int, private var _maximum: Int, private var _questId: Int) {
  def itemId: Int = _itemId

  def chance: Int = _chance

  def continentId: Int = _continentId

  def minimum: Int = _minimum

  def maximum: Int = _maximum

  def questId: Int = _questId
}
