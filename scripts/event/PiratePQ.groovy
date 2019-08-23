package event

import client.MapleCharacter
import net.server.world.MapleParty
import net.server.world.MaplePartyCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import server.life.MapleMonster
import server.maps.MapleMap

import java.awt.*
import java.util.List

class EventPiratePQ {
   EventManager em
   boolean isPq = true
   boolean isGrindMode = false
   int minPlayers = 3, maxPlayers = 6
   int minLevel = 55, maxLevel = 100
   int entryMap = 925100000
   int exitMap = 925100700
   int recruitMap = 251010404
   int clearMap = 925100600
   int minMapId = 925100000
   int maxMapId = 925100500
   int eventTime = 4
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
      List<Integer> itemSet = [4001117, 4001120, 4001121, 4001122]
      eim.setExclusiveItems(itemSet)
   }

   // sets all possible treasures that can be given, randomly, to a player at the end of the event.
   def setEventRewards(EventInstanceManager eim) {
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
   def setup(int level, int lobbyid) {
      EventInstanceManager eim = em.newInstance("Pirate" + lobbyid)
      eim.setProperty("level", level)

      eim.setProperty("stage2", "0")
      eim.setProperty("stage2a", "0")
      eim.setProperty("stage3a", "0")
      eim.setProperty("stage2b", "0")
      eim.setProperty("stage3b", "0")
      eim.setProperty("stage4", "0")
      eim.setProperty("stage5", "0")

      eim.setProperty("curStage", "1")
      eim.setProperty("grindMode", isGrindMode ? "1" : "0")

      eim.setProperty("openedChests", "0")
      eim.setProperty("openedBoxes", "0")
      eim.getInstanceMap(925100000).resetPQ(level)
      eim.getInstanceMap(925100000).shuffleReactors()

      eim.getInstanceMap(925100100).resetPQ(level)
      MapleMap map = eim.getInstanceMap(925100200)
      map.resetPQ(level)
      map.shuffleReactors()
      for (int i = 0; i < 5; i++) {
         MapleMonster mob = em.getMonster(9300124)
         MapleMonster mob2 = em.getMonster(9300125)
         MapleMonster mob3 = em.getMonster(9300124)
         MapleMonster mob4 = em.getMonster(9300125)
         eim.registerMonster(mob)
         eim.registerMonster(mob2)
         eim.registerMonster(mob3)
         eim.registerMonster(mob4)
         mob.changeDifficulty(level, isPq)
         mob2.changeDifficulty(level, isPq)
         mob3.changeDifficulty(level, isPq)
         mob4.changeDifficulty(level, isPq)
         map.spawnMonsterOnGroundBelow(mob, new Point(430, 75))
         map.spawnMonsterOnGroundBelow(mob2, new Point(1600, 75))
         map.spawnMonsterOnGroundBelow(mob3, new Point(430, 238))
         map.spawnMonsterOnGroundBelow(mob4, new Point(1600, 238))
      }
      map = eim.getInstanceMap(925100201)
      map.resetPQ(level)
      for (int i = 0; i < 10; i++) {
         MapleMonster mob = em.getMonster(9300112)
         MapleMonster mob2 = em.getMonster(9300113)
         eim.registerMonster(mob)
         eim.registerMonster(mob2)
         mob.changeDifficulty(level, isPq)
         mob2.changeDifficulty(level, isPq)
         map.spawnMonsterOnGroundBelow(mob, new Point(0, 238))
         map.spawnMonsterOnGroundBelow(mob2, new Point(1700, 238))
      }
      eim.getInstanceMap(925100202).resetPQ(level)
      map = eim.getInstanceMap(925100300)
      map.resetPQ(level)
      map.shuffleReactors()
      for (int i = 0; i < 5; i++) {
         MapleMonster mob = em.getMonster(9300124)
         MapleMonster mob2 = em.getMonster(9300125)
         MapleMonster mob3 = em.getMonster(9300124)
         MapleMonster mob4 = em.getMonster(9300125)
         eim.registerMonster(mob)
         eim.registerMonster(mob2)
         eim.registerMonster(mob3)
         eim.registerMonster(mob4)
         mob.changeDifficulty(level, isPq)
         mob2.changeDifficulty(level, isPq)
         mob3.changeDifficulty(level, isPq)
         mob4.changeDifficulty(level, isPq)
         map.spawnMonsterOnGroundBelow(mob, new Point(430, 75))
         map.spawnMonsterOnGroundBelow(mob2, new Point(1600, 75))
         map.spawnMonsterOnGroundBelow(mob3, new Point(430, 238))
         map.spawnMonsterOnGroundBelow(mob4, new Point(1600, 238))
      }
      map = eim.getInstanceMap(925100301)
      map.resetPQ(level)
      for (int i = 0; i < 10; i++) {
         MapleMonster mob = em.getMonster(9300112)
         MapleMonster mob2 = em.getMonster(9300113)
         eim.registerMonster(mob)
         eim.registerMonster(mob2)
         mob.changeDifficulty(level, isPq)
         mob2.changeDifficulty(level, isPq)
         map.spawnMonsterOnGroundBelow(mob, new Point(0, 238))
         map.spawnMonsterOnGroundBelow(mob2, new Point(1700, 238))
      }
      eim.getInstanceMap(925100302).resetPQ(level)
      eim.getInstanceMap(925100400).resetPQ(level)
      eim.getInstanceMap(925100500).resetPQ(level)

      respawnStages(eim)

      eim.startEventTimer(eventTime * 60000)
      setEventRewards(eim)
      setEventExclusives(eim)
      return eim
   }

   // Happens after the event instance is initialized and all players have been assigned for the event instance, but before entrying players.
   def afterSetup(EventInstanceManager eim) {
   }

   // Defines which maps inside the event are allowed to respawn. This function should create a new task at the end of it's body calling itself at a given respawn rate.
   static def respawnStages(EventInstanceManager eim) {
      int stg = eim.getIntProperty("stage2")
      if (stg < 3) {  // thanks Chloek3, seth1, BHB for suggesting map respawn rather than waves on stg2
         eim.getMapInstance(925100100).spawnAllMonsterIdFromMapSpawnList(9300114 + stg, eim.getIntProperty("level"), true)
      }

      eim.getMapInstance(925100400).instanceMapRespawn()
      eim.schedule("respawnStages", 10 * 1000)
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

   static def changedMapInside(EventInstanceManager eim, int mapid) {
      int stage = eim.getIntProperty("curStage")

      if (stage == 1) {
         if (mapid == 925100100) {
            eim.restartEventTimer(6 * 60 * 1000)
            eim.setIntProperty("curStage", 2)
         }
      } else if (stage == 2) {
         if (mapid == 925100200) {
            eim.restartEventTimer(6 * 60 * 1000)
            eim.setIntProperty("curStage", 3)
         }
      } else if (stage == 3) {
         if (mapid == 925100300) {
            eim.restartEventTimer(6 * 60 * 1000)
            eim.setIntProperty("curStage", 4)
         }
      } else if (stage == 4) {
         if (mapid == 925100400) {
            eim.restartEventTimer(6 * 60 * 1000)
            eim.setIntProperty("curStage", 5)
         }
      } else if (stage == 5) {
         if (mapid == 925100500) {
            eim.restartEventTimer(8 * 60 * 1000)
            eim.setIntProperty("curStage", 6)
         }
      }
   }

   // What to do when player've changed map, based on the mapid.
   def changedMap(EventInstanceManager eim, MapleCharacter player, int mapid) {
      if (mapid < minMapId || mapid > maxMapId) {
         if (eim.isEventTeamLackingNow(true, minPlayers, player)) {
            eim.unregisterPlayer(player)
            end(eim)
         } else {
            eim.unregisterPlayer(player)
         }
      } else {
         changedMapInside(eim, mapid)
      }
   }

   // Do something if the party leader has been changed.
   def changedLeader(EventInstanceManager eim, MapleCharacter leader) {
      int mapid = leader.getMapId()
      if (!eim.isEventCleared() && (mapid < minMapId || mapid > maxMapId)) {
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
            player.dropMessage(6, "You have run out of time to complete this event!")
            playerExit(eim, player)
         }
      }
      eim.dispose()
   }

   // Happens when an opposing mob dies
   static def monsterKilled(MapleMonster mob, EventInstanceManager eim) {
      MapleMap map = mob.getMap()

      if (isLordPirate(mob)) {  // lord pirate defeated, spawn the little fella!
         map.broadcastStringMessage(5, "As Lord Pirate dies, Wu Yang is released!")
         eim.spawnNpc(2094001, new Point(777, 140), mob.getMap())
      }

      if (map.countMonsters() == 0) {
         int stage = ((map.getId() % 1000) / 100).intValue() + 1

         if ((stage == 1 || stage == 3 || stage == 4) && passedGrindMode(map, eim)) {
            eim.showClearEffect(map.getId())
         } else if (stage == 5) {
            if (map.getReactorByName("sMob1").getState() >= 1 && map.getReactorByName("sMob2").getState() >= 1 && map.getReactorByName("sMob3").getState() >= 1 && map.getReactorByName("sMob4").getState() >= 1) {
               eim.showClearEffect(map.getId())
            }
         }
      }
   }

   // Invoked when a monster that's registered has been killed
   // return x amount for this player - "Saved Points"
   static def monsterValue(EventInstanceManager eim, int mobid) {
      return 1
   }

   // Happens when a friendly mob dies
   def friendlyKilled(MapleMonster mob, EventInstanceManager eim) {
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
         end(eim)
      } else {
         playerExit(eim, player)
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
   def giveRandomEventReward(EventInstanceManager eim, MapleCharacter player) {
   }

   // Happens when the party succeeds on completing the event instance.
   static def clearPQ(EventInstanceManager eim) {
      eim.stopEventTimer()
      eim.setEventCleared()

      int chests = (eim.getProperty("openedChests")).toInteger()
      int expGain = (chests == 0 ? 28000 : (chests == 1 ? 35000 : 42000))
      eim.giveEventPlayersExp(expGain)

      eim.warpEventTeam(925100600)
   }

   static def isLordPirate(MapleMonster mob) {
      int mobid = mob.getId()
      return (mobid == 9300105) || (mobid == 9300106) || (mobid == 9300107) || (mobid == 9300119)
   }

   static def passedGrindMode(MapleMap map, EventInstanceManager eim) {
      if (eim.getIntProperty("grindMode") == 0) {
         return true
      }
      return eim.activatedAllReactorsOnMap(map, 2511000, 2517999)
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

   // Happens when the funtion NPCConversationManager.removePlayerFromInstance() is invoked
   def removePlayer(EventInstanceManager eim, MapleCharacter player) {
   }

   // Happens when carnival PQ is started. - Unused for now.
   def registerCarnivalParty(EventInstanceManager eim, MapleParty carnivalparty) {
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

EventPiratePQ getEvent() {
   if (!getBinding().hasVariable("event")) {
      EventManager em = (EventManager) getBinding().getVariable("em")
      getBinding().setVariable("event", new EventPiratePQ(em: em))
   }
   return (EventPiratePQ) getBinding().getVariable("event")
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

def setup(int level, int lobbyid) {
   getEvent().setup(level, lobbyid)
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

def changedMap(EventInstanceManager eim, MapleCharacter player, int mapid) {
   getEvent().changedMap(eim, player, mapid)
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

def monsterValue(EventInstanceManager eim, int mobid) {
   getEvent().monsterValue(eim, mobid)
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

def registerCarnivalParty(EventInstanceManager eim, MapleParty carnivalparty) {
   getEvent().registerCarnivalParty(eim, carnivalparty)
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