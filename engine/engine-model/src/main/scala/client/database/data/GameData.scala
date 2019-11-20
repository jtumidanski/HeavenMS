package client.database.data

import client.database.GameType

class GameData(private var _type: GameType, private var _wins: Int, private var _losses: Int, private var _ties: Int) {
  def theType: GameType = _type

  def wins: Int = _wins

  def losses: Int = _losses

  def ties: Int = _ties

  def incrementWins(): Unit = {
    _wins += 1
  }

  def incrementLosses(): Unit = {
    _losses += 1
  }

  def incrementTies(): Unit = {
    _ties += 1
  }
}
