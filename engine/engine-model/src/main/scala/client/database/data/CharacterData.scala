package client.database.data

import java.sql.Timestamp
import java.util.Date

class CharacterData(private var _accountId: Int, private var _id: Int, private var _name: String,
                    private var _gender: Int, private var _skinColor: Int, private var _face: Int,
                    private var _hair: Int, private var _level: Int, private var _job: Int, private var _str: Int,
                    private var _dex: Int, private var _intelligence: Int, private var _luk: Int,
                    private var _hp: Int, private var _maxHp: Int, private var _mp: Int, private var _maxMp: Int,
                    private var _ap: Int, private var _sp: Array[String], private var _exp: Int,
                    private var _fame: Int, private var _gachaponExp: Int, private var _map: Int,
                    private var _spawnPoint: Int, private var _gm: Int, private var _world: Int,
                    private var _rank: Int, private var _rankMove: Int, private var _jobRank: Int,
                    private var _jobRankMove: Int, private var _questFame: Int, private var _hpMpUsed: Int,
                    private var _merchant: Boolean, private var _meso: Int, private var _merchantMeso: Int,
                    private var _finishedDojoTutorial: Boolean, private var _vanquisherKills: Int,
                    private var _vanquisherStage: Int, private var _omok: GameData, private var _matchCard: GameData,
                    private var _jailExpire: Long, private var _mountExp: Int, private var _mountLevel: Int,
                    private var _mountTiredness: Int, private var _guildId: Int, private var _guildRank: Int,
                    private var _allianceRank: Int, private var _familyId: Int, private var _monsterBookCover: Int,
                    private var _ariantPoints: Int, private var _dojoPoints: Int, private var _lastDojoStage: Int,
                    private var _dataString: String, private var _lastExpGainTime: Date, private var _partyInvite: Boolean,
                    private var _equipSlotLimit: Int, private var _useSlotLimit: Int, private var _setupSlotLimit: Int,
                    private var _etcSlotLimit: Int, private var _partnerId: Int, private var _marriageItemId: Int,
                    private var _partyId: Int, private var _messengerId: Int, private var _messengerPosition: Int) {
  def accountId: Int = _accountId

  def id: Int = _id

  def name: String = _name

  def gender: Int = _gender

  def skinColor: Int = _skinColor

  def face: Int = _face

  def hair: Int = _hair

  def level: Int = _level

  def job: Int = _job

  def str: Int = _str

  def dex: Int = _dex

  def intelligence: Int = _intelligence

  def luk: Int = _luk

  def hp: Int = _hp

  def maxHp: Int = _maxHp

  def mp: Int = _mp

  def maxMp: Int = _maxMp

  def ap: Int = _ap

  def sp: Array[String] = _sp

  def exp: Int = _exp

  def fame: Int = _fame

  def gachaponExp: Int = _gachaponExp

  def map: Int = _map

  def spawnPoint: Int = _spawnPoint

  def gm: Int = _gm

  def world: Int = _world

  def rank: Int = _rank

  def rankMove: Int = _rankMove

  def jobRank: Int = _jobRank

  def jobRankMove: Int = _jobRankMove

  def questFame: Int = _questFame

  def hpMpUsed: Int = _hpMpUsed

  def merchant: Boolean = _merchant

  def meso: Int = _meso

  def merchantMeso: Int = _merchantMeso

  def finishedDojoTutorial: Boolean = _finishedDojoTutorial

  def vanquisherKills: Int = _vanquisherKills

  def vanquisherStage: Int = _vanquisherStage

  def omok: GameData = _omok

  def matchCard: GameData = _matchCard

  def jailExpire: Long = _jailExpire

  def mountExp: Int = _mountExp

  def mountLevel: Int = _mountLevel

  def mountTiredness: Int = _mountTiredness

  def guildId: Int = _guildId

  def guildRank: Int = _guildRank

  def allianceRank: Int = _allianceRank

  def familyId: Int = _familyId

  def monsterBookCover: Int = _monsterBookCover

  def ariantPoints: Int = _ariantPoints

  def dojoPoints: Int = _dojoPoints

  def lastDojoStage: Int = _lastDojoStage

  def dataString: String = _dataString

  def lastExpGainTime: Timestamp = _lastExpGainTime.asInstanceOf[Timestamp]

  def partyInvite: Boolean = _partyInvite

  def equipSlotLimit: Int = _equipSlotLimit

  def useSlotLimit: Int = _useSlotLimit

  def setupSlotLimit: Int = _setupSlotLimit

  def etcSlotLimit: Int = _etcSlotLimit

  def partnerId: Int = _partnerId

  def marriageItemId: Int = _marriageItemId

  def partyId: Int = _partyId

  def messengerId: Int = _messengerId

  def messengerPosition: Int = _messengerPosition

  def this(world: Int, name: String, level: Int) = {
    this(0, 0, name, 0, 0, 0, 0, level, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Array[String](), 0, 0, 0, 0, 0, 0, world, 0, 0,
      0, 0, 0, 0, false, 0, 0, false, 0, 0, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", null, false, 0, 0,
      0, 0, 0, 0, 0, 0, 0)
  }

  def this(name: String, level: Int) = {
    this(0, 0, name, 0, 0, 0, 0, level, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Array[String](), 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, false, 0, 0, false, 0, 0, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", null, false, 0, 0,
      0, 0, 0, 0, 0, 0, 0)
  }
}
