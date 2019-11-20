package client.inventory

import java.util

import constants.ServerConstants
import tools.Pair

class Equip(_id: Int, _position: Short, private var _slots: Int, _elemental: Boolean)
  extends Item(_id, _position, 1) {

  var itemExp: Float = 0

  var itemLevel: Byte = 1

  var level: Byte = 0

  var str: Int = 0

  var dex: Int = 0

  var _int: Int = 0

  var luk: Int = 0

  var hp: Int = 0

  var mp: Int = 0

  var watk: Int = 0

  var matk: Int = 0

  var wdef: Int = 0

  var mdef: Int = 0

  var acc: Int = 0

  var avoid: Int = 0

  var hands: Int = 0

  var speed: Int = 0

  var jump: Int = 0

  var vicious: Int = 0

  var ringId: Int = -1

  var wearing: Boolean = false

  var upgradeable: Boolean = false

  var slots: Int = _slots

  def elemental: Boolean = _elemental

  def this(id: Int, position: Short, elemental: Boolean) = {
    this(id, position, 0, elemental)
  }

  override def quantity_=(_quantity: Short): Unit = {
    if (_quantity < 0 || _quantity > 1) throw new RuntimeException("Setting the quantity to " + _quantity + " on an equip (itemid: " + id + ")")
    super.quantity_=(_quantity)
  }

  override def copy(): Item = {
    val ret: Equip = new Equip(id, position, slots, elemental)
    ret.str = str
    ret.dex = dex
    ret._int = _int
    ret.luk = luk
    ret.hp = hp
    ret.mp = mp
    ret.watk = watk
    ret.matk = matk
    ret.wdef = wdef
    ret.mdef = mdef
    ret.acc = acc
    ret.avoid = avoid
    ret.hands = hands
    ret.speed = speed
    ret.jump = jump
    ret.flag = flag
    ret.vicious = vicious
//    ret._slots = _slots
    ret.itemLevel = itemLevel
    ret.itemExp = itemExp
    ret.level = level
    ret.log = log
    ret.owner = owner
    ret.quantity = quantity
    ret._expiration = _expiration
    ret.giftFrom = giftFrom
    ret
  }

  override def itemType: Byte = 1

  def getStats: java.util.Map[StatUpgrade, java.lang.Short] = {
    val stats: util.Map[StatUpgrade, java.lang.Short] = new util.HashMap[StatUpgrade, java.lang.Short](5)

    if (dex > 0) stats.put(StatUpgrade.incDEX, dex.asInstanceOf[Short])
    if (str > 0) stats.put(StatUpgrade.incSTR, str.asInstanceOf[Short])
    if (_int > 0) stats.put(StatUpgrade.incINT, _int.asInstanceOf[Short])
    if (luk > 0) stats.put(StatUpgrade.incLUK, luk.asInstanceOf[Short])
    if (hp > 0) stats.put(StatUpgrade.incMHP, hp.asInstanceOf[Short])
    if (mp > 0) stats.put(StatUpgrade.incMMP, mp.asInstanceOf[Short])
    if (watk > 0) stats.put(StatUpgrade.incPAD, watk.asInstanceOf[Short])
    if (matk > 0) stats.put(StatUpgrade.incMAD, matk.asInstanceOf[Short])
    if (wdef > 0) stats.put(StatUpgrade.incPDD, wdef.asInstanceOf[Short])
    if (mdef > 0) stats.put(StatUpgrade.incMDD, mdef.asInstanceOf[Short])
    if (avoid > 0) stats.put(StatUpgrade.incEVA, avoid.asInstanceOf[Short])
    if (acc > 0) stats.put(StatUpgrade.incACC, acc.asInstanceOf[Short])
    if (speed > 0) stats.put(StatUpgrade.incSpeed, speed.asInstanceOf[Short])
    if (jump > 0) stats.put(StatUpgrade.incJump, jump.asInstanceOf[Short])

    stats
  }

  def gainStats(stats: java.util.List[Pair[StatUpgrade, Integer]]): Pair[String, Pair[java.lang.Boolean, java.lang.Boolean]] = {
    var gotSlot: Boolean = false
    var gotVicious: Boolean = false
    var statUp: Int = ServerConstants.MAX_EQUIPMNT_STAT
    val maxStat: Int = ServerConstants.MAX_EQUIPMNT_STAT
    val lvupStr: StringBuilder = new StringBuilder

    stats.forEach((stat: Pair[StatUpgrade, Integer]) => {
      stat.getLeft match {
        case incDEX =>
          statUp = Math.min(stat.getRight, maxStat - dex)
          dex += statUp
          lvupStr.append("+").append(statUp).append("DEX ")
        case incSTR =>
          statUp = Math.min(stat.getRight, maxStat - str)
          str += statUp
          lvupStr.append("+").append(statUp).append("STR ")
        case incINT =>
          statUp = Math.min(stat.getRight, maxStat - _int)
          _int += statUp
          lvupStr.append("+").append(statUp).append("INT ")
        case incLUK =>
          statUp = Math.min(stat.getRight, maxStat - luk)
          luk += statUp
          lvupStr.append("+").append(statUp).append("LUK ")
        case incMHP =>
          statUp = Math.min(stat.getRight, maxStat - hp)
          hp += statUp
          lvupStr.append("+").append(statUp).append("HP ")
        case incMMP =>
          statUp = Math.min(stat.getRight, maxStat - mp)
          mp += statUp
          lvupStr.append("+").append(statUp).append("MP ")
        case incPAD =>
          statUp = Math.min(stat.getRight, maxStat - watk)
          watk += statUp
          lvupStr.append("+").append(statUp).append("WATK ")
        case incMAD =>
          statUp = Math.min(stat.getRight, maxStat - matk)
          matk += statUp
          lvupStr.append("+").append(statUp).append("MATK ")
        case incPDD =>
          statUp = Math.min(stat.getRight, maxStat - wdef)
          wdef += statUp
          lvupStr.append("+").append(statUp).append("WDEF ")
        case incMDD =>
          statUp = Math.min(stat.getRight, maxStat - mdef)
          mdef += statUp
          lvupStr.append("+").append(statUp).append("MDEF ")
        case incEVA =>
          statUp = Math.min(stat.getRight, maxStat - avoid)
          avoid += statUp
          lvupStr.append("+").append(statUp).append("AVOID ")
        case incACC =>
          statUp = Math.min(stat.getRight, maxStat - acc)
          acc += statUp
          lvupStr.append("+").append(statUp).append("ACC ")
        case incSpeed =>
          statUp = Math.min(stat.getRight, maxStat - speed)
          speed += statUp
          lvupStr.append("+").append(statUp).append("SPEED ")
        case incJump =>
          statUp = Math.min(stat.getRight, maxStat - jump)
          jump += statUp
          lvupStr.append("+").append(statUp).append("JUMP ")
        case incVicious =>
          vicious -= stat.getRight
          gotVicious = true
        case incSlot =>
          _slots += stat.getRight
          gotSlot = true
      }
    })
    new Pair[String, Pair[java.lang.Boolean, java.lang.Boolean]](lvupStr.toString(), new Pair[java.lang.Boolean, java.lang.Boolean](gotSlot, gotVicious))
  }
}
