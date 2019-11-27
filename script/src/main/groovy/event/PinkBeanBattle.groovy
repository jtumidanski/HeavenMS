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
import server.maps.MapleReactor
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*
import java.util.List

class EventPinkBeanBattle {
   EventManager em
   boolean isPq = true
   int minPlayers = 6, maxPlayers = 30
   int minLevel = 120, maxLevel = 255
   int entryMap = 270050100
   int exitMap = 270050300
   int recruitMap = 270050000
   int clearMap = 270050300
   int minMapId = 270050100
   int maxMapId = 270050300
   int eventTime = 140
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
      List<Integer> itemSet = []
      eim.setExclusiveItems(itemSet)
   }

   // sets all possible treasures that can be given, randomly, to a player at the end of the event.
   static def setEventRewards(EventInstanceManager eim) {
      int evLevel = 1    //Rewards at clear PQ
      List<Integer> itemSet = []
      List<Integer> itemQty = []
      eim.setEventRewards(evLevel, itemSet, itemQty)

      List<Integer> expStages = []    //bonus exp given on CLEAR stage signal
      eim.setEventClearStageExp(expStages)

      List<Integer> mesoStages = []    //bonus meso given on CLEAR stage signal
      eim.setEventClearStageMeso(mesoStages)
   }

   // selects, from the given party, the team that is allowed to attempt this event
   def getEligibleParty(MaplePartyCharacter[] party) {
   }

   // Setup the instance when invoked, EG : start PQ
   def setup(int channel) {
      EventInstanceManager eim = em.newInstance("PinkBean" + channel)
      eim.setProperty("canJoin", 1)
      eim.setProperty("defeatedBoss", 0)
      eim.setProperty("fallenPlayers", 0)

      eim.setProperty("stage", 1)
      eim.setProperty("channel", channel)

      int level = 1
      eim.getInstanceMap(270050100).resetPQ(level)
      eim.getInstanceMap(270050200).resetPQ(level)
      eim.getInstanceMap(270050300).resetPQ(level)

      MapleMonster mob = MapleLifeFactory.getMonster(8820000)
      mob.disableDrops()
      eim.getInstanceMap(270050100).spawnMonsterOnGroundBelow(mob, new Point(0, -42))

      eim.startEventTimer(eventTime * 60000)
      setEventRewards(eim)
      setEventExclusives(eim)

      return eim
   }

   // Happens after the event instance is initialized and all players have been assigned for the event instance, but before entrying players.
   static def afterSetup(EventInstanceManager eim) {
      MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "The first wave will start within 15 seconds, prepare yourselves.")
      eim.schedule("startWave", 15 * 1000)
   }

   // Defines which maps inside the event are allowed to respawn. This function should create a new task at the end of it's body calling itself at a given respawn rate.
   def respawnStages(EventInstanceManager eim) {
   }

   // Warp player in etc..
   def playerEntry(EventInstanceManager eim, MapleCharacter player) {
      MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] " + player.getName() + " has entered the map.")
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
   }

   // What to do when player've changed map, based on the mapid.
   def changedMap(EventInstanceManager eim, MapleCharacter player, int mapid) {
      if (mapid < minMapId || mapid > maxMapId) {
         if (eim.isExpeditionTeamLackingNow(true, minPlayers, player)) {
            eim.unregisterPlayer(player)
            MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] Either the leader has quit the expedition or there is no longer the minimum number of members required to continue it.")
            end(eim)
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] " + player.getName() + " has left the expedition.")
            eim.unregisterPlayer(player)
         }
      }
   }

   // Do something if the party leader has been changed.
   def changedLeader(EventInstanceManager eim, MapleCharacter leader) {
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
      if (isPinkBean(mob)) {
         eim.setIntProperty("defeatedBoss", 1)
         eim.showClearEffect(mob.getMap().getId())
         mob.getMap().killAllMonsters()
         eim.clearPQ()

         int ch = eim.getIntProperty("channel")
         mob.getMap().broadcastPinkBeanVictory(ch)
      } else if (isJrBoss(mob)) {
         if (noJrBossesLeft(mob.getMap())) {
            int stage = eim.getIntProperty("stage")

            if (stage == 5) {
               int iid = 4001193
               Item itemObj = new Item(iid, (short) 0, (short) 1)
               MapleMap mapObj = eim.getMapFactory().getMap(270050100)
               MapleReactor reactObj = mapObj.getReactorById(2708000)
               MapleCharacter dropper = eim.getPlayers().get(0)
               mapObj.spawnItemDrop(dropper, dropper, itemObj, reactObj.position(), true, true)

               MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, "With the last of its guardians fallen, Pink Bean loses its invulnerability. The real fight starts now!")
            } else {
               stage++
               eim.setIntProperty("stage", stage)

               MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "The next wave will start within 15 seconds, prepare yourselves.")
               eim.schedule("startWave", 15 * 1000)
            }
         }
      }
   }

   static def startWave(EventInstanceManager eim) {
      MapleMap mapObj = eim.getMapInstance(270050100)
      int stage = eim.getProperty("stage").toInteger()

      for (int i = 1; i <= stage; i++) {
         spawnJrBoss(mapObj.getMonsterById(8820019 + (i % 5)), false)
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
      int count = eim.getIntProperty("fallenPlayers")
      count = count + 1

      eim.setIntProperty("fallenPlayers", count)

      if (count == 5) {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] Too many players have fallen, Pink Bean is now deemed undefeatable; the expedition is over.")
         end(eim)
      } else if (count == 4) {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] Pink Bean is growing stronger than ever, last stand mode everyone!")
      } else if (count == 3) {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] Casualty count is starting to get out of control. Battle with care.")
      }
   }

   // Happens when an opposing mob revives
   static def monsterRevive(MapleMonster mob, EventInstanceManager eim) {
      if (isPinkBean(mob)) {
         mob.enableDrops()
      }
   }

   // Happens when player's revived.
   // @Param : returns true/false
   static def playerRevive(EventInstanceManager eim, MapleCharacter player) {
      return true
   }

   // return 0 - Deregister player normally + Dispose instance if there are zero player left
   // return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
   // return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
   def playerDisconnected(EventInstanceManager eim, MapleCharacter player) {
      if (eim.isExpeditionTeamLackingNow(true, minPlayers, player)) {
         eim.unregisterPlayer(player)
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] Either the leader has quit the expedition or there is no longer the minimum number of members required to continue it.")
         end(eim)
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] " + player.getName() + " has left the expedition.")
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

   static def isPinkBean(MapleMonster mob) {
      int mobid = mob.id()
      return (mobid == 8820001)
   }

   static def isJrBoss(MapleMonster mob) {
      int mobid = mob.id()
      return (mobid >= 8820002 && mobid <= 8820006)
   }

   static def noJrBossesLeft(map) {
      return map.countMonster(8820002, 8820006) == 0
   }

   static def spawnJrBoss(MapleMonster mobObj, boolean gotKilled) {
      int spawnid
      if (gotKilled) {
         spawnid = mobObj.id() + 17

      } else {
         mobObj.getMap().killMonster(mobObj.id())
         spawnid = mobObj.id() - 17
      }

      MapleMonster mob = MapleLifeFactory.getMonster(spawnid)
      mobObj.getMap().spawnMonsterOnGroundBelow(mob, mobObj.position())
   }

   // Happens when a player left the party
   def leftParty(EventInstanceManager eim, MapleCharacter player) {
   }

   // Happens when the party is disbanded by the leader.
   def disbandParty(EventInstanceManager eim, MapleCharacter player) {
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

EventPinkBeanBattle getEvent() {
   if (!getBinding().hasVariable("event")) {
      EventManager em = (EventManager) getBinding().getVariable("em")
      getBinding().setVariable("event", new EventPinkBeanBattle(em: em))
   }
   return (EventPinkBeanBattle) getBinding().getVariable("event")
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

def setup(int channel) {
   getEvent().setup(channel)
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