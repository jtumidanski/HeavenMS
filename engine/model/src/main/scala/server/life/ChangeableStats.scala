package server.life

import constants.MobConstants

class ChangeableStats private extends OverrideMonsterStats {
  var watk: Int = 0

  var matk: Int = 0

  var wdef: Int = 0

  var mdef: Int = 0

  var level: Int = 0

  def this(mapleMonsterStats: MapleMonsterStats, overrideMonsterStats: OverrideMonsterStats) = {
    this()
    hp = overrideMonsterStats.hp
    mp = overrideMonsterStats.mp
    exp = overrideMonsterStats.exp
    watk = mapleMonsterStats.paDamage
    matk = mapleMonsterStats.maDamage
    wdef = mapleMonsterStats.pdDamage
    mdef = mapleMonsterStats.mdDamage
    level = mapleMonsterStats.level
  }

  def this(mapleMonsterStats: MapleMonsterStats, newLevel: Int, pqMob: Boolean) = {
    this()
    val mod: Double = newLevel / mapleMonsterStats.level
    val hpRatio: Double = mapleMonsterStats.hp / mapleMonsterStats.exp
    val pqMod: Double = if (pqMob) 1.5 else 1.0

    val hpRound: Int = Math.round(if (!mapleMonsterStats.isBoss) MobConstants.getMonsterHP(newLevel) else mapleMonsterStats.hp * mod).asInstanceOf[Int]
    hp = Math.min(hpRound, Integer.MAX_VALUE)

    val expRound: Int = Math.round(if (!mapleMonsterStats.isBoss) MobConstants.getMonsterHP(newLevel) / hpRatio else mapleMonsterStats.exp).asInstanceOf[Int]
    exp = Math.min(expRound, Integer.MAX_VALUE)

    val mpRound: Int = Math.round(mapleMonsterStats.mp * mod * pqMod).asInstanceOf[Int]
    mp = Math.min(mpRound, Integer.MAX_VALUE)

    watk = Math.min(Math.round(mapleMonsterStats.paDamage * mod).asInstanceOf[Int], Integer.MAX_VALUE)
    matk = Math.min(Math.round(mapleMonsterStats.maDamage * mod).asInstanceOf[Int], Integer.MAX_VALUE)
    wdef = Math.min(Math.min(if (mapleMonsterStats.isBoss) 30 else 20, Math.round(mapleMonsterStats.pdDamage * mod).asInstanceOf[Int]), Integer.MAX_VALUE)
    mdef = Math.min(Math.min(if (mapleMonsterStats.isBoss) 30 else 20, Math.round(mapleMonsterStats.mdDamage * mod).asInstanceOf[Int]), Integer.MAX_VALUE)
    level = newLevel
  }

  def this(mapleMonsterStats: MapleMonsterStats, statModifier: Float, pqMob: Boolean) = {
    this(mapleMonsterStats, (statModifier * mapleMonsterStats.level).asInstanceOf[Int], pqMob)
  }
}
