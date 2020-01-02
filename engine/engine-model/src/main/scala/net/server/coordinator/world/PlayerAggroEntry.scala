package net.server.coordinator.world

class PlayerAggroEntry(private var _cid: Int, var averageDamage: Int, var currentDamageInstances: Int,
                       var accumulatedDamage: Long, var expireStreak: Int, var updateStreak: Int, var toNextUpdate: Int,
                       var entryRank: Int) {
  def cid: Int = _cid

  def this(_cid: Int) = {
    this(_cid, 0, 0, 0, 0, 0, 0, -1)
  }
}
