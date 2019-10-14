package event

import client.MapleCharacter
import client.inventory.Item
import net.server.world.MapleParty
import net.server.world.MaplePartyCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*
import java.util.List

class EventHolidayPQ_2 {
   EventManager em
   boolean isPq = true
   int minPlayers = 3, maxPlayers = 6
   int minLevel = 31, maxLevel = 40
   int entryMap = 889100011
   int exitMap = 889100012
   int recruitMap = 889100010
   int clearMap = 889100012
   int minMapId = 889100011
   int maxMapId = 889100011
   int eventTime = 20
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
      List<Integer> itemSet = [4032094, 4032095]
      eim.setExclusiveItems(itemSet)
   }

   // sets all possible treasures that can be given, randomly, to a player at the end of the event.
   static def setEventRewards(EventInstanceManager eim) {
      List<Integer> itemSet, itemQty, expStages
      int evLevel

      evLevel = 3    //Rewards at Hard difficulty
      itemSet = [1302080, 1002033, 2022153, 2022042, 2020006, 2020009, 2020016, 2020024, 4010006, 4010007, 4020004, 4020005, 4003002]
      itemQty = [1, 1, 1, 5, 20, 15, 10, 10, 2, 4, 4, 4, 1]
      eim.setEventRewards(evLevel, itemSet, itemQty)

      evLevel = 2    //Rewards at Normal difficulty
      itemSet = [1302080, 1002033, 2012005, 2012006, 2020002, 2020025, 2020026, 4010003, 4010004, 4010005, 4020002, 4020003, 4020007]
      itemQty = [1, 1, 15, 15, 15, 10, 10, 3, 3, 3, 3, 3, 3]
      eim.setEventRewards(evLevel, itemSet, itemQty)

      evLevel = 1    //Rewards at Easy difficulty
      itemSet = [1002033, 2012005, 2012006, 2020002, 2022006, 2022002, 4010000, 4010001, 4010002, 4020000, 4020001, 4020006]
      itemQty = [1, 15, 15, 10, 5, 5, 2, 2, 2, 2, 2, 2]
      eim.setEventRewards(evLevel, itemSet, itemQty)

      expStages = [210, 620, 500, 1400, 950, 2200]    //bonus exp given on CLEAR stage signal
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
   def setup(int level, int lobbyid) {
      EventInstanceManager eim = em.newInstance("Holiday2_" + lobbyid)
      eim.setProperty("level", level)
      eim.setProperty("stage", "0")
      eim.setProperty("statusStg1", "-1")
      eim.setProperty("missingDrops", "0")
      eim.setProperty("snowmanLevel", "0")
      eim.setProperty("snowmanStep", "0")
      eim.setProperty("spawnedBoss", "0")

      MapleMap mapobj = eim.getInstanceMap(entryMap)
      mapobj.resetPQ(level)
      mapobj.allowSummonState(false)

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
   def respawnStages(EventInstanceManager eim) {
      eim.getInstanceMap(entryMap).instanceMapRespawn()
      eim.schedule("respawnStages", 10 * 1000)
   }

   def snowmanHeal(EventInstanceManager eim) {
      int difficulty = eim.getIntProperty("level")
      MapleMonster snowman = eim.getInstanceMap(entryMap).getMonsterById(9400316 + (5 * difficulty) + 5)

      snowman.heal(200 + 200 * difficulty, 0)
      eim.schedule("snowmanHeal", 10 * 1000)
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

   // What to do when player've changed map, based on the mapid.
   def changedMap(EventInstanceManager eim, MapleCharacter player, int mapid) {
      if (mapid < minMapId || mapid > maxMapId) {
         if (eim.isEventTeamLackingNow(true, minPlayers, player)) {
            eim.unregisterPlayer(player)
            end(eim)
         } else {
            eim.unregisterPlayer(player)
         }
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
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "You have run out of time to complete this event!")
            playerExit(eim, player)
         }
      }
      eim.dispose()
   }

   // Happens when an opposing mob dies
   static def monsterKilled(MapleMonster mob, EventInstanceManager eim) {
      if (eim.isEventCleared()) {
         return
      } else if (isScrooge(mob)) {
         eim.giveEventPlayersStageReward(2 * eim.getIntProperty("level"))
         eim.showClearEffect()
         eim.clearPQ()
         return
      }

      double rnd = Math.random()
      boolean forceDrop = false
      if (rnd >= 0.42) {   // 42% chance of dropping token
         int miss = eim.getIntProperty("missingDrops")
         if (miss < 5) {
            eim.setIntProperty("missingDrops", miss + 1)
            return
         }

         forceDrop = true
      }

      MapleMap mapObj = mob.getMap()
      Item itemObj = new Item((forceDrop || Math.random() < 0.77) ? 4032094 : 4032095, (short) 0, (short) 1)
      // 77% chance of not fake
      MapleCharacter dropper = eim.getPlayers().get(0)

      mapObj.spawnItemDrop(mob, dropper, itemObj, mob.position(), true, false)
      eim.setIntProperty("missingDrops", 0)
   }

   // Invoked when a monster that's registered has been killed
   // return x amount for this player - "Saved Points"
   static def monsterValue(EventInstanceManager eim, int mobid) {
      return 1
   }

   // Happens when a friendly mob dies
   def friendlyKilled(MapleMonster mob, EventInstanceManager eim) {
      eim.setIntProperty("snowmanStep", 0)
      int snowmanLevel = eim.getIntProperty("snowmanLevel")

      if (snowmanLevel <= 1) {
         end(eim)
      } else {
         eim.setIntProperty("snowmanLevel", snowmanLevel - 1)
      }
   }

   def snowmanEvolve(EventInstanceManager eim, int curLevel) {
      MapleMap mapobj = eim.getInstanceMap(entryMap)
      int difficulty = eim.getIntProperty("level")
      MapleMonster snowman = mapobj.getMonsterById(9400317 + (5 * difficulty) + (curLevel - 1))

      eim.setIntProperty("snowmanLevel", curLevel + 2)   // increment by 2 to decrement by 1 on friendlyKilled
      mapobj.killMonster(snowman, null, false, 2)

      snowman = MapleLifeFactory.getMonster(9400317 + (5 * difficulty) + curLevel)
      mapobj.spawnMonsterOnGroundBelow(snowman, new Point(-180, 15))

      if (curLevel >= 4) {
         mapobj.allowSummonState(false)
         mapobj.killAllMonstersNotFriendly()
         mapobj.setReactorState()

         eim.giveEventPlayersStageReward(2 * difficulty - 1)
         eim.showClearEffect()
      }
   }

   def snowmanSnack(EventInstanceManager eim) {
      if (eim.getIntProperty("snowmanLevel") >= 5) {
         return
      }

      int step = eim.getIntProperty("snowmanStep")
      int snowmanLevel = eim.getIntProperty("snowmanLevel")

      if (step >= 2 + (eim.getIntProperty("level") * snowmanLevel)) {
         step = 0
         snowmanEvolve(eim, snowmanLevel)
      } else {
         MapleMap mapobj = eim.getInstanceMap(entryMap)
         int difficulty = eim.getIntProperty("level")
         MapleMonster snowman = mapobj.getMonsterById(9400316 + (5 * difficulty) + snowmanLevel)

         snowman.heal(200 + (200 * snowmanLevel), 0)
         step += 1
      }

      eim.setIntProperty("snowmanStep", step)
   }

   static def snowmanSnackFake(EventInstanceManager eim) {
      if (eim.getIntProperty("snowmanLevel") >= 5) {
         return
      }

      int step = eim.getIntProperty("snowmanStep")
      if (step > 0) {
         eim.setIntProperty("snowmanStep", step - 1)
      }

      MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "The snowman absorbed a Fake Snow Vigor!")
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

      eim.applyEventPlayersItemBuff(2022437)
   }

   static def isScrooge(MapleMonster mob) {
      int mobid = mob.id()
      return mobid >= 9400319 && mobid <= 9400321
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

EventHolidayPQ_2 getEvent() {
   if (!getBinding().hasVariable("event")) {
      EventManager em = (EventManager) getBinding().getVariable("em")
      getBinding().setVariable("event", new EventHolidayPQ_2(em: em))
   }
   return (EventHolidayPQ_2) getBinding().getVariable("event")
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