package server.life

import java.util
import java.util.Collections
import java.util.function.Predicate

import tools.Pair

import scala.collection.immutable.HashMap

class MapleMonsterStats() {
  private var _changeable: Boolean = false

  private var _exp: Int = 0

  var hp: Int = 0

  private var _mp: Int = 0

  private var _level: Int = 0

  private var _removeAfter: Int = 0

  var dropPeriod: Int = 0

  var isBoss: Boolean = false

  private var _isFFALoot: Boolean = false

  private var _isUndead: Boolean = false

  private var _name: String = ""

  var tagColor: Byte = 0

  var tagBackgroundColor: Byte = 0

  var isFirstAttack: Boolean = false

  private var _buffToGive: Int = -1

  private var _banish: Option[BanishInfo] = Option.empty

  private var _paDamage: Int = 0

  private var _pdDamage: Int = 0

  private var _maDamage: Int = 0

  private var _mdDamage: Int = 0

  private var _cp: Int = 0

  private var _isExplosiveReward: Boolean = false

  private var _selfDestruction: Option[SelfDestruction] = Option.empty

  private var _removeOnMiss: Boolean = false

  var cool: Pair[Int, Int] = _

  private var _isFriendly: Boolean = false

  var fixedStance: Int = 0

  private var animationTimes: Map[String, Int] = new HashMap[String, Int]()

  var revives: java.util.List[Integer] = Collections.emptyList()

  private var _resistance: Map[Element, ElementalEffectiveness] = new HashMap[Element, ElementalEffectiveness]()

  private var _skills: java.util.List[Pair[Integer, Integer]] = new util.ArrayList[Pair[Integer, Integer]]()

  private var _loseItems: java.util.List[LoseItem] = new util.LinkedList[LoseItem]()

  def changeable: Boolean = _changeable

  def exp: Int = _exp

  def mp: Int = _mp

  def level: Int = _level

  //noinspection MutatorLikeMethodIsParameterless
  def removeAfter: Int = _removeAfter

  def isFFALoot: Boolean = _isFFALoot

  def isUndead: Boolean = _isUndead

  def name: String = _name

  def buffToGive: Int = _buffToGive

  def banish: Option[BanishInfo] = _banish

  def banish_=(banishInfo: BanishInfo) = Option.apply(banishInfo)

  def paDamage: Int = _paDamage

  def pdDamage: Int = _pdDamage

  def maDamage: Int = _maDamage

  def mdDamage: Int = _mdDamage

  def cp: Int = _cp

  def isExplosiveReward: Boolean = _isExplosiveReward

  def selfDestruction: Option[SelfDestruction] = _selfDestruction

  def selfDestruction_=(selfDestruction: SelfDestruction): Unit = _selfDestruction = Option.apply(selfDestruction)

  //noinspection MutatorLikeMethodIsParameterless
  def removeOnMiss: Boolean = _removeOnMiss

  def isFriendly: Boolean = _isFriendly

  def init(hp: Int, isFriendly: Boolean, paDamage: Int, pdDamage: Int, maDamage: Int, mdDamage: Int, mp: Int, exp: Int,
           level: Int, removeAfter: Int, isBoss: Boolean, isExplosiveReward: Boolean, isFFALoot: Boolean,
           isUndead: Boolean, name: String, buffToGive: Int, cp: Int, removeOnMiss: Boolean): Unit = {
    this.hp = hp
    this._isFriendly = _isFriendly
    this._paDamage = _paDamage
    this._pdDamage = _pdDamage
    this._maDamage = _maDamage
    this._mdDamage = _mdDamage
    this._mp = _mp
    this._exp = _exp
    this._level = _level
    this._removeAfter = removeAfter
    this.isBoss = isBoss
    this._isExplosiveReward = _isExplosiveReward
    this._isFFALoot = _isFFALoot
    this._isUndead = _isUndead
    this._name = _name
    this._buffToGive = _buffToGive
    this._cp = _cp
    this._removeOnMiss = _removeOnMiss
  }

  def setAnimationTime(name: String, delay: Int): Unit = {
    animationTimes = animationTimes + (name -> delay)
  }

  def getAnimationTime(name: String): Int = {
    val result: Option[Int] = animationTimes.get(name)
    if (result.isEmpty) {
      return 500
    }
    result.get
  }

  def isMobile: Boolean = animationTimes.contains("move") || animationTimes.contains("fly")

  def setEffectiveness(element: Element, elementalEffectiveness: ElementalEffectiveness): Unit = {
    _resistance = _resistance + (element -> elementalEffectiveness)
  }

  def getEffectiveness(element: Element): ElementalEffectiveness = {
    val elementalEffectiveness: Option[ElementalEffectiveness] = _resistance.get(element)
    if (elementalEffectiveness.isDefined) {
      elementalEffectiveness.get
    } else {
      ElementalEffectiveness.NORMAL
    }
  }

  def removeEffectiveness(element: Element): Unit = {
    _resistance = _resistance.removed(element)
  }

  def getSkills: java.util.List[Pair[Integer, Integer]] = Collections.unmodifiableList(_skills)

  //TODO this needs to be reviewed
  def setSkills(skills: java.util.List[Pair[Integer, Integer]]): Unit = {
    /*
      for (int i = this.skills.size(); i < skills.size(); i++) {
         this.skills.add(null);
      }

      for (int i = 0; i < skills.size(); i++) {
         this.skills.set(i, skills.get(i));
      }
     */
    _skills = new util.ArrayList[Pair[Integer, Integer]]()
    skills.forEach((t: Pair[Integer, Integer]) => {
      _skills.add(t)
    })
  }

  def getNoSkills: Int = _skills.size()

  def hasSkill(skillId: Int, level: Int): Boolean = {
    _skills.stream().anyMatch(new Predicate[Pair[Integer, Integer]] {
      override def test(t: Pair[Integer, Integer]): Boolean = {
        if (t.getLeft == skillId && t.getRight == level) {
          return true
        }
        false
      }
    })
  }

  def loseItem(): java.util.List[LoseItem] = _loseItems

  def addLoseItem(loseItem: LoseItem): Boolean = _loseItems.add(loseItem)

  def copy: MapleMonsterStats = {
    val copyStats: MapleMonsterStats = new MapleMonsterStats()
    copyStats._changeable = _changeable
    copyStats._exp = _exp
    copyStats.hp = hp
    copyStats._mp = _mp
    copyStats._level = _level
    copyStats._removeAfter = _removeAfter
    copyStats.dropPeriod = dropPeriod
    copyStats.isBoss = isBoss
    copyStats._isFFALoot = _isFFALoot
    copyStats._isUndead = _isUndead
    copyStats._name = _name
    copyStats.tagColor = tagColor
    copyStats.tagBackgroundColor = tagBackgroundColor
    copyStats.isFirstAttack = isFirstAttack
    copyStats._buffToGive = _buffToGive
    copyStats._banish = _banish
    copyStats._paDamage = _paDamage
    copyStats._pdDamage = _pdDamage
    copyStats._maDamage = _maDamage
    copyStats._mdDamage = _mdDamage
    copyStats._cp = _cp
    copyStats._isExplosiveReward = _isExplosiveReward
    copyStats._selfDestruction = _selfDestruction
    copyStats._removeOnMiss = _removeOnMiss
    copyStats.cool = cool
    copyStats._isFriendly = _isFriendly
    copyStats.fixedStance = fixedStance
    copyStats.animationTimes = animationTimes
    copyStats.revives = revives
    copyStats._resistance = _resistance
    copyStats._skills = _skills
    copyStats._loseItems = _loseItems
    copyStats
  }

}
