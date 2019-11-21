package client.creator

import java.util.concurrent.atomic.AtomicInteger

import client.MapleJob
import client.inventory.{Item, MapleInventoryType}
import config.YamlConfig
import tools.Pair

class CharacterFactoryRecipe(private var _job: MapleJob, private var _level: Int, private var _map: Int, private var _top: Int, private var _bottom: Int, private var _shoes: Int, private var _weapon: Int) {
  var str: Int = 4

  var dex: Int = 4

  var intelligence: Int = 4

  var luk: Int = 4

  var maxHp: Int = 50

  var maxMp: Int = 5

  var remainingAp: Int = 0

  var remainingSp: Int = 0

  var meso: Int = 0

  private var _skills: List[Pair[Integer, Integer]] = List()

  private var _itemsWithType: List[Pair[Item, MapleInventoryType]] = List()

  private var _runningTypePosition: Map[MapleInventoryType, AtomicInteger] = Map()

  if (!YamlConfig.config.server.USE_STARTING_AP_4) {
    if (YamlConfig.config.server.USE_AUTOASSIGN_STARTERS_AP) {
      str = 12
      dex = 5
    } else {
      remainingAp = 9
    }
  }

  def job: MapleJob = _job

  def level: Int = _level

  def map: Int = _map

  def top: Int = _top

  def bottom: Int = _bottom

  def shoes: Int = _shoes

  def weapon: Int = _weapon

  def addStartingSkillLevel(skillId: Int, level: Int): Unit = {
    _skills = _skills.appended(new Pair(skillId, level))
  }

  def addStartingEquipment(item: Item): Unit = {
    _itemsWithType = _itemsWithType.appended(new Pair(item, MapleInventoryType.EQUIP))
  }

  def addStartingItem(itemId: Int, quantity: Int, itemType: MapleInventoryType): Unit = {
    val p: Option[AtomicInteger] = _runningTypePosition get itemType
    if (p.isEmpty) {
      _runningTypePosition = _runningTypePosition updated(itemType, new AtomicInteger(0))
    }
  }

  def getStartingSkillLevel: List[Pair[Integer, Integer]] = _skills

  def getStartingItems: List[Pair[Item, MapleInventoryType]] = _itemsWithType
}
