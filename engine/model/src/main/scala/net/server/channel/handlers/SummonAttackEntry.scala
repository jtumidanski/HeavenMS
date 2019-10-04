package net.server.channel.handlers

class SummonAttackEntry(private var _monsterObjectId: Int, private var _damage: Int) {
  def monsterObjectId: Int = _monsterObjectId

  def damage: Int = _damage
}
