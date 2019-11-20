package server

import java.util.function.Predicate

import tools.Pair

class CardItemupStats(private var _itemCode: Int, private var _probability: Int, private var _areas: java.util.List[Pair[Integer, Integer]], private var _inParty: Boolean) {
  def itemCode: Int = _itemCode

  def probability: Int = _probability

  def areas: java.util.List[Pair[Integer, Integer]] = _areas

  def inParty: Boolean = _inParty

  def isInArea(mapId: Int): Boolean = {
    if (_areas == null) {
      return true
    }

    _areas.stream().filter(new Predicate[Pair[Integer, Integer]] {
      override def test(t: Pair[Integer, Integer]): Boolean = {
        if (mapId >= t.left && mapId <= t.right) {
          return true
        }
        false
      }
    }).findFirst().isPresent
  }
}
