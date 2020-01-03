package event

import client.MapleCharacter
import net.server.world.MapleParty
import net.server.world.MaplePartyCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.life.MapleNPCFactory
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*
import java.util.List

class EventMagatiaPQ_Z {
   EventManager em
   boolean isPq = true
   int minPlayers = 4, maxPlayers = 4
   int minLevel = 71, maxLevel = 85
   int entryMap = 926100000
   int exitMap = 926100700
   int recruitMap = 261000011
   int clearMap = 926100700
   int minMapId = 926100000
   int maxMapId = 926100600
   int eventTime = 45
   int[] lobbyRange = [0, 0]

   // After loading, ChannelServer
   def init() {
      setEventRequirements()
   }

   def setLobbyRange() {
      return lobbyRange
   }

   // sets requirement info about the event to be displayed at the recruitment area.
   def setEventRequirements() {
      String reqStr = ""

      reqStr += "\r\n    Number of players: "
      if (maxPlayers - minPlayers >= 1) {
         reqStr += minPlayers + " ~ " + maxPlayers
      } else {
         reqStr += minPlayers
      }

      reqStr += "\r\n    Level range: "
      if (maxLevel - minLevel >= 1) {
         reqStr += minLevel + " ~ " + maxLevel
      } else {
         reqStr += minLevel
      }

      reqStr += "\r\n    Time limit: "
      reqStr += eventTime + " minutes"

      em.setProperty("party", reqStr)
   }

   // sets all items that should exist only for the event instance, and that should be removed from inventory at the end of the run.
   static def setEventExclusives(EventInstanceManager eim) {
      List<Integer> itemSet = [4001130, 4001131, 4001132, 4001133, 4001134, 4001135]
      eim.setExclusiveItems(itemSet)
   }

   // sets all possible treasures that can be given, randomly, to a player at the end of the event.
   static def setEventRewards(EventInstanceManager eim) {
      int evLevel = 1    //Rewards at clear PQ
      List<Integer> itemSet = [2000003, 2000002, 2000004, 2000005, 2022003, 1032016, 1032015, 1032014, 2041212, 2041020, 2040502, 2041016, 2044701, 2040301, 2043201, 2040501, 2040704, 2044001, 2043701, 2040803, 1102026, 1102028, 1102029]
      List<Integer> itemQty = [100, 100, 20, 10, 50, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
      eim.setEventRewards(evLevel, itemSet, itemQty)

      List<Integer> expStages = [0, 10000, 20000, 0, 20000, 20000, 0, 0]    //bonus exp given on CLEAR stage signal
      eim.setEventClearStageExp(expStages)
   }

   // selects, from the given party, the team that is allowed to attempt this event
   def getEligibleParty(MaplePartyCharacter[] party) {
      MaplePartyCharacter[] eligible = []
      boolean hasLeader = false

      if (party.size() > 0) {
         MaplePartyCharacter[] partyList = party

         for (int i = 0; i < party.size(); i++) {
            MaplePartyCharacter ch = partyList[i]

            if (ch.getMapId() == recruitMap && ch.getLevel() >= minLevel && ch.getLevel() <= maxLevel) {
               if (ch.isLeader()) {
                  hasLeader = true
               }
               eligible << ch
            }
         }
      }

      if (!(hasLeader && eligible.length >= minPlayers && eligible.length <= maxPlayers)) {
         eligible = []
      }
      return eligible
   }

   // Setup the instance when invoked, EG : start PQ
   def setup(int level, int lobbyId) {
      EventInstanceManager eim = em.newInstance("MagatiaZ" + lobbyId)
      eim.setProperty("level", level)

      eim.setIntProperty("isAlcadno", 0)

      eim.setIntProperty("escortFail", 0)
      eim.setIntProperty("yuleteTimeout", 0)
      eim.setIntProperty("yuleteTalked", 0)
      eim.setIntProperty("yuletePassed", 0)
      eim.setIntProperty("npcShocked", 0)
      eim.setIntProperty("normalClear", 0)

      eim.setIntProperty("statusStg1", 0)
      eim.setIntProperty("statusStg2", 0)
      eim.setIntProperty("statusStg3", 0)
      eim.setIntProperty("statusStg4", 0)
      eim.setIntProperty("statusStg5", 0)
      eim.setIntProperty("statusStg6", 0)
      eim.setIntProperty("statusStg7", 0)

      eim.getInstanceMap(926100000).resetPQ(level)
      eim.getInstanceMap(926100001).resetPQ(level)
      eim.getInstanceMap(926100100).resetPQ(level)
      eim.getInstanceMap(926100200).resetPQ(level)
      eim.getInstanceMap(926100201).resetPQ(level)
      eim.getInstanceMap(926100202).resetPQ(level)
      eim.getInstanceMap(926100203).resetPQ(level)
      eim.getInstanceMap(926100300).resetPQ(level)
      eim.getInstanceMap(926100301).resetPQ(level)
      eim.getInstanceMap(926100302).resetPQ(level)
      eim.getInstanceMap(926100303).resetPQ(level)
      eim.getInstanceMap(926100304).resetPQ(level)
      eim.getInstanceMap(926100400).resetPQ(level)
      eim.getInstanceMap(926100401).resetPQ(level)
      eim.getInstanceMap(926100500).resetPQ(level)
      eim.getInstanceMap(926100600).resetPQ(level)
      eim.getInstanceMap(926100700).resetPQ(level)

      eim.getInstanceMap(926100201).shuffleReactors(2518000, 2612004)
      eim.getInstanceMap(926100202).shuffleReactors(2518000, 2612004)

      MapleNPCFactory.spawnNpc(2112000, new Point(252, 243), eim.getInstanceMap(926100203))
      MapleNPCFactory.spawnNpc(2112000, new Point(200, 100), eim.getInstanceMap(926100401))
      MapleNPCFactory.spawnNpc(2112001, new Point(200, 100), eim.getInstanceMap(926100500))
      MapleNPCFactory.spawnNpc(2112018, new Point(200, 100), eim.getInstanceMap(926100600))

      respawnStages(eim)
      eim.startEventTimer(eventTime * 60000)
      setEventRewards(eim)
      setEventExclusives(eim)
      return eim
   }

   static def shuffle(int[] array) {
      int currentIndex = array.length, temporaryValue, randomIndex

      // While there remain elements to shuffle...
      while (0 != currentIndex) {

         // Pick a remaining element...
         randomIndex = Math.floor(Math.random() * currentIndex).intValue()
         currentIndex -= 1

         // And swap it with the current element.
         temporaryValue = array[currentIndex]
         array[currentIndex] = array[randomIndex]
         array[randomIndex] = temporaryValue
      }

      return array
   }

   static def generateStg6Combo(EventInstanceManager eim) {
      List<List<Integer>> matrix = []

      for (int i = 0; i < 4; i++) {
         matrix.push([])
      }

      for (int j = 0; j < 10; j++) {
         int[] array = [0, 1, 2, 3]
         array = shuffle(array)

         for (int i = 0; i < 4; i++) {
            matrix[i].push(array[i])
         }
      }

      for (int i = 0; i < 4; i++) {
         String comb = ""
         for (int j = 0; j < 10; j++) {
            int r = matrix[i][j]
            comb += r.toString()
         }

         eim.setProperty("stage6_comb" + (i + 1), comb)
      }
   }

   // Happens after the event instance is initialized and all players have been assigned for the event instance, but before entering players.
   static def afterSetup(EventInstanceManager eim) {
      eim.setIntProperty("escortFail", 0)    // refresh friendly status

      int[] books = [-1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 2, 3]
      shuffle(books)

      eim.setIntProperty("stg1_b0", books[0])
      eim.setIntProperty("stg1_b1", books[1])
      eim.setIntProperty("stg1_b2", books[2])
      eim.setIntProperty("stg1_b3", books[3])
      eim.setIntProperty("stg1_b4", books[4])
      eim.setIntProperty("stg1_b5", books[5])
      eim.setIntProperty("stg1_b6", books[6])
      eim.setIntProperty("stg1_b7", books[7])
      eim.setIntProperty("stg1_b8", books[8])
      eim.setIntProperty("stg1_b9", books[9])
      eim.setIntProperty("stg1_b10", books[10])
      eim.setIntProperty("stg1_b11", books[11])
      eim.setIntProperty("stg1_b12", books[12])
      eim.setIntProperty("stg1_b13", books[13])
      eim.setIntProperty("stg1_b14", books[14])
      eim.setIntProperty("stg1_b15", books[15])
      eim.setIntProperty("stg1_b16", books[16])
      eim.setIntProperty("stg1_b17", books[17])
      eim.setIntProperty("stg1_b18", books[18])
      eim.setIntProperty("stg1_b19", books[19])
      eim.setIntProperty("stg1_b20", books[20])
      eim.setIntProperty("stg1_b21", books[21])
      eim.setIntProperty("stg1_b22", books[22])
      eim.setIntProperty("stg1_b23", books[23])
      eim.setIntProperty("stg1_b24", books[24])
      eim.setIntProperty("stg1_b25", books[25])
   }

   // Defines which maps inside the event are allowed to respawn. This function should create a new task at the end of it's body calling itself at a given respawn rate.
   static def respawnStages(EventInstanceManager eim) {
      eim.getMapInstance(926100100).instanceMapRespawn()
      eim.getMapInstance(926100200).instanceMapRespawn()

      if (!eim.isEventCleared()) {
         MapleMap map = eim.getMapInstance(926100401)
         int mobCount = map.countMonster(9300150)
         if (mobCount == 0) {
            MapleLifeFactory.getMonster(9300150).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(-278, -126)) })
            MapleLifeFactory.getMonster(9300150).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(-542, -126)) })
         } else if (mobCount == 1) {
            MapleLifeFactory.getMonster(9300150).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(-542, -126)) })
         }
      }

      eim.schedule("respawnStages", 15 * 1000)
   }

   // Warp player in etc..
   def playerEntry(EventInstanceManager eim, MapleCharacter player) {
      MapleMap map = eim.getMapInstance(entryMap)
      player.changeMap(map, map.getPortal(0))
   }

   // Do something with the player that is about to unregister right before unregistering he/she.
   def playerUnregistered(EventInstanceManager eim, MapleCharacter player) {
   }

   // Do something with the player right before disbanding the event instance.
   def playerExit(EventInstanceManager eim, MapleCharacter player) {
      eim.unregisterPlayer(player)
      player.changeMap(exitMap, 0)
   }

   // Do something with the player right before leaving the party.
   def playerLeft(EventInstanceManager eim, MapleCharacter player) {
      if (!eim.isEventCleared()) {
         playerExit(eim, player)
      }
   }

   // What to do when player've changed map, based on the mapId.
   def changedMap(EventInstanceManager eim, MapleCharacter player, int mapId) {
      if (mapId < minMapId || mapId > maxMapId) {
         if (eim.isEventTeamLackingNow(true, minPlayers, player)) {
            eim.unregisterPlayer(player)
            end(eim)
         } else {
            eim.unregisterPlayer(player)
         }

      } else if (mapId == 926100203 && eim.getIntProperty("yuleteTimeout") == 0) {
         eim.setIntProperty("yuleteTimeout", 1)
         eim.schedule("yuleteAction", 10 * 1000)
      }
   }

   static def yuleteAction(EventInstanceManager eim) {
      if (eim.getIntProperty("yuleteTalked") == 1) {
         eim.setIntProperty("yuletePassed", 1)

         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "Yulete: Ugh, you guys disgust me. All I desired was to make this nation the greatest alchemy powerhouse of the entire world. If they won't accept this, I will make it true by myself, at any costs!!!")
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "Yulete: Hahaha... Did you really think I was going to be so unprepared knowing that the Magatia societies' dogs would be coming in my pursuit after my actions? Fools!")
      }
      eim.setIntProperty("yuleteTalked", -1)

      MapleMap map = eim.getMapInstance(926100203)
      int mob1 = 9300143, mob2 = 9300144

      map.destroyNPC(2112000)

      for (int i = 0; i < 5; i++) {
         MapleLifeFactory.getMonster(mob1).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(-455, 135)) })
         MapleLifeFactory.getMonster(mob2).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(-455, 135)) })
      }

      for (int i = 0; i < 5; i++) {
         MapleLifeFactory.getMonster(mob1).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(0, 135)) })
         MapleLifeFactory.getMonster(mob2).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(0, 135)) })
      }

      for (int i = 0; i < 5; i++) {
         MapleLifeFactory.getMonster(mob1).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(360, 135)) })
         MapleLifeFactory.getMonster(mob2).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(360, 135)) })
      }
   }

   // Do something if the party leader has been changed.
   def changedLeader(EventInstanceManager eim, MapleCharacter leader) {
      int mapId = leader.getMapId()
      if (!eim.isEventCleared() && (mapId < minMapId || mapId > maxMapId)) {
         end(eim)
      }
   }

   // When event timeout without before completion..
   def scheduledTimeout(EventInstanceManager eim) {
      end(eim)
   }

   def timeOut(EventInstanceManager eim) {
      if (eim.getPlayerCount() > 0) {
         Iterator<MapleCharacter> pIter = eim.getPlayers().iterator()
         while (pIter.hasNext()) {
            MapleCharacter player = pIter.next()
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "You have run out of time to complete this event!")
            playerExit(eim, player)
         }
      }
      eim.dispose()
   }

   // Happens when an opposing mob dies
   static def monsterKilled(MapleMonster mob, EventInstanceManager eim) {
      MapleMap map = mob.getMap()

      if (map.getId() == 926100001 && eim.getIntProperty("statusStg1") == 1) {
         if (map.countMonsters() == 0) {
            eim.showClearEffect()
            eim.giveEventPlayersStageReward(2)
            eim.setIntProperty("statusStg2", 1)
         }
      } else if (map.getId() == 926100203 && eim.getIntProperty("statusStg1") == 1) {
         if (map.countMonsters() == 0) {
            eim.showClearEffect()
            eim.giveEventPlayersStageReward(5)

            generateStg6Combo(eim)
            map.getReactorByName("rnj6_out").forceHitReactor((byte) 1)
         }
      } else if (mob.id() == 9300139 || mob.id() == 9300140) {
         eim.showClearEffect()
         eim.giveEventPlayersStageReward(7)

         MapleNPCFactory.spawnNpc(2112006, new Point(-370, -150), map)

         int gain = (eim.getIntProperty("escortFail") == 1) ? 90000 : ((mob.id() == 9300139) ? 105000 : 140000)
         eim.giveEventPlayersExp(gain)

         map.killAllMonstersNotFriendly()

         if (mob.id() == 9300139) {
            eim.setIntProperty("normalClear", 1)
         }

         eim.clearPQ()
      }
   }

   // Invoked when a monster that's registered has been killed
   // return x amount for this player - "Saved Points"
   static def monsterValue(EventInstanceManager eim, int mobId) {
      return 1
   }

   // Happens when a friendly mob dies
   static def friendlyKilled(MapleMonster mob, EventInstanceManager eim) {
      eim.setIntProperty("escortFail", 1)
   }

   // When invoking unregisterMonster(MapleMonster mob) OR killed
   // Happens only when size = 0
   def allMonstersDead(EventInstanceManager eim) {
   }

   // Happens when player dies
   def playerDead(EventInstanceManager eim, player) {
   }

   // Happens when an opposing mob revives
   def monsterRevive(mob, EventInstanceManager eim) {
   }

   // Happens when player's revived.
   // @Param : returns true/false
   def playerRevive(EventInstanceManager eim, MapleCharacter player) {
      if (eim.isEventTeamLackingNow(true, minPlayers, player)) {
         eim.unregisterPlayer(player)
         end(eim)
      } else {
         eim.unregisterPlayer(player)
      }
   }

   // return 0 - Deregister player normally + Dispose instance if there are zero player left
   // return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
   // return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
   def playerDisconnected(EventInstanceManager eim, MapleCharacter player) {
      if (eim.isEventTeamLackingNow(true, minPlayers, player)) {
         eim.unregisterPlayer(player)
         end(eim)
      } else {
         eim.unregisterPlayer(player)
      }
   }

   // Happens when the party fails to complete the event instance.
   def end(EventInstanceManager eim) {
      List<MapleCharacter> party = eim.getPlayers()
      for (int i = 0; i < party.size(); i++) {
         playerExit(eim, party.get(i))
      }
      eim.dispose()
   }

   // Selects randomly a reward to give from the reward pool.
   static def giveRandomEventReward(EventInstanceManager eim, MapleCharacter player) {
      eim.giveEventReward(player)
   }

   // Happens when the party succeeds on completing the event instance.
   static def clearPQ(EventInstanceManager eim) {
      eim.stopEventTimer()
      eim.setEventCleared()
   }

   // Happens when a player left the party
   def leftParty(EventInstanceManager eim, MapleCharacter player) {
      if (eim.isEventTeamLackingNow(false, minPlayers, player)) {
         end(eim)
      } else {
         playerLeft(eim, player)
      }
   }

   // Happens when the party is disbanded by the leader.
   def disbandParty(EventInstanceManager eim, MapleCharacter player) {
      if (!eim.isEventCleared()) {
         end(eim)
      }
   }

   // Happens when the function NPCConversationManager.removePlayerFromInstance() is invoked
   def removePlayer(EventInstanceManager eim, MapleCharacter player) {
   }

   // Happens when carnival PQ is started. - Unused for now.
   def registerCarnivalParty(EventInstanceManager eim, MapleParty carnivalParty) {
   }

   // Happens when player change map - Unused for now.
   def onMapLoad(EventInstanceManager eim, MapleCharacter player) {
   }

   // Finishes ongoing schedules.
   def cancelSchedule() {
   }

   // Finishes the event instance.
   def dispose() {
   }
}

EventMagatiaPQ_Z getEvent() {
   if (!getBinding().hasVariable("event")) {
      EventManager em = (EventManager) getBinding().getVariable("em")
      getBinding().setVariable("event", new EventMagatiaPQ_Z(em: em))
   }
   return (EventMagatiaPQ_Z) getBinding().getVariable("event")
}

def init(EventInstanceManager eim) {
   getEvent().init()
}

def setLobbyRange(EventInstanceManager eim) {
   return getEvent().setLobbyRange()
}

def setEventRequirements(EventInstanceManager eim) {
   getEvent().setEventRequirements()
}

def setEventExclusives(EventInstanceManager eim) {
   getEvent().setEventExclusives(eim)
}

def setEventRewards(EventInstanceManager eim) {
   getEvent().setEventRewards(eim)
}

def getEligibleParty(MaplePartyCharacter[] party) {
   getEvent().getEligibleParty(party)
}

def setup(int level, int lobbyId) {
   getEvent().setup(level, lobbyId)
}

def afterSetup(EventInstanceManager eim) {
   getEvent().afterSetup(eim)
}

def respawnStages(EventInstanceManager eim) {
   getEvent().respawnStages(eim)
}

def playerEntry(EventInstanceManager eim, MapleCharacter player) {
   getEvent().playerEntry(eim, player)
}

def playerUnregistered(EventInstanceManager eim, MapleCharacter player) {
   getEvent().playerUnregistered(eim, player)
}

def playerExit(EventInstanceManager eim, MapleCharacter player) {
   getEvent().playerExit(eim, player)
}

def playerLeft(EventInstanceManager eim, MapleCharacter player) {
   getEvent().playerLeft(eim, player)
}

def changedMap(EventInstanceManager eim, MapleCharacter player, int mapId) {
   getEvent().changedMap(eim, player, mapId)
}

def changedLeader(EventInstanceManager eim, MapleCharacter leader) {
   getEvent().changedLeader(eim, leader)
}

def scheduledTimeout(EventInstanceManager eim) {
   getEvent().scheduledTimeout(eim)
}

def timeOut(EventInstanceManager eim) {
   getEvent().timeOut(eim)
}

def monsterKilled(MapleMonster mob, EventInstanceManager eim) {
   getEvent().monsterKilled(mob, eim)
}

def monsterValue(EventInstanceManager eim, int mobId) {
   getEvent().monsterValue(eim, mobId)
}

def friendlyKilled(MapleMonster mob, EventInstanceManager eim) {
   getEvent().friendlyKilled(mob, eim)
}

def allMonstersDead(EventInstanceManager eim) {
   getEvent().allMonstersDead(eim)
}

def playerDead(EventInstanceManager eim, MapleCharacter player) {
   getEvent().playerDead(eim, player)
}

def monsterRevive(MapleMonster mob, EventInstanceManager eim) {
   getEvent().monsterRevive(mob, eim)
}

def playerRevive(EventInstanceManager eim, MapleCharacter player) {
   getEvent().playerRevive(eim, player)
}

def playerDisconnected(EventInstanceManager eim, MapleCharacter player) {
   getEvent().playerDisconnected(eim, player)
}

def end(EventInstanceManager eim) {
   getEvent().end(eim)
}

def giveRandomEventReward(EventInstanceManager eim, MapleCharacter player) {
   getEvent().giveRandomEventReward(eim, player)
}

def clearPQ(EventInstanceManager eim) {
   getEvent().clearPQ(eim)
}

def leftParty(EventInstanceManager eim, MapleCharacter player) {
   getEvent().leftParty(eim, player)
}

def disbandParty(EventInstanceManager eim, MapleCharacter player) {
   getEvent().disbandParty(eim, player)
}

def removePlayer(EventInstanceManager eim, MapleCharacter player) {
   getEvent().removePlayer(eim, player)
}

def registerCarnivalParty(EventInstanceManager eim, MapleParty carnivalParty) {
   getEvent().registerCarnivalParty(eim, carnivalParty)
}

def onMapLoad(EventInstanceManager eim, MapleCharacter player) {
   getEvent().onMapLoad(eim, player)
}

def cancelSchedule(EventInstanceManager eim) {
   getEvent().cancelSchedule()
}

def dispose(EventInstanceManager eim) {
   getEvent().dispose()
}