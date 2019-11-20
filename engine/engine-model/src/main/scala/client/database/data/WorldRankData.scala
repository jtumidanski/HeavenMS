package client.database.data

import scala.jdk.CollectionConverters._

class WorldRankData(private var _worldId: Int) {
  def worldId: Int = _worldId

  private var _userRanks: List[GlobalUserRank] = List[GlobalUserRank]()

  def userRanks: List[GlobalUserRank] = _userRanks

  def addUserRank(userRank: GlobalUserRank): Unit = {
    _userRanks = _userRanks.appended(userRank)
  }

  def getUserRanks: java.util.List[GlobalUserRank] = {
    _userRanks.asJava
  }
}
