package server

class QuestConsItem(private var _questId: Int, private var _exp: Int, private var _grade: Int,
                    private var _items: java.util.Map[java.lang.Integer, java.lang.Integer]) {
  def questId: Int = _questId

  def exp: Int = _exp

  def grade: Int = _grade

  def items: java.util.Map[java.lang.Integer, java.lang.Integer] = _items
}
