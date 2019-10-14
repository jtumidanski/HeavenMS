package event

import client.MapleCharacter
import net.server.world.MapleParty
import net.server.world.MaplePartyCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import server.life.MapleMonster
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

class EventScargaBattle {
   EventManager em
   boolean isPq = true
   int minPlayers = 6, maxPlayers = 30
   int minLevel = 100, maxLevel = 255
   int entryMap = 551030200
   int exitMap = 551030100
   int recruitMap = 551030100
   int clearMap = 551030100
   int minMapId = 551030200
   int maxMapId = 551030200
   int eventTime = 60
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
      List<Integer> itemSet = [1102145, 1102084, 1102085, 1102086, 1102087, 1052165, 1052166, 1052167, 1402013, 1332030, 1032030, 1032070, 4003000, 4000030, 4006000, 4006001, 4005000, 4005001, 4005002, 4005003, 4005004, 2022016, 2022263, 2022264, 2022015, 2022306, 2022307, 2022306, 2022113]
      List<Integer> itemQty = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 50, 50, 120, 120, 4, 4, 4, 4, 2, 125, 125, 125, 30, 30, 30, 30, 30]
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
      EventInstanceManager eim = em.newInstance("Scarga" + channel)
      eim.setProperty("canJoin", 1)
      eim.setProperty("defeatedBoss", 0)

      int level = 1
      eim.getInstanceMap(551030200).resetPQ(level)

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
            MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] Either the leader has quit the expedition or there is no longer the minimum number of members required to continue it.")
            eim.unregisterPlayer(player)
            end(eim)
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] " + player.getName() + " has left the instance.")
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
      if (isScarga(mob)) {
         int killed = eim.getIntProperty("defeatedBoss")
         if (killed == 1) {
            eim.showClearEffect()
            eim.clearPQ()
         }

         eim.setIntProperty("defeatedBoss", killed + 1)
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
      if (eim.isExpeditionTeamLackingNow(true, minPlayers, player)) {
         eim.unregisterPlayer(player)
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] Either the leader has quit the expedition or there is no longer the minimum number of members required to continue it.")
         end(eim)
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] " + player.getName() + " has left the instance.")
         eim.unregisterPlayer(player)
      }
   }

   // return 0 - Deregister player normally + Dispose instance if there are zero player left
   // return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
   // return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
   def playerDisconnected(EventInstanceManager eim, MapleCharacter player) {
      if (eim.isExpeditionTeamLackingNow(true, minPlayers, player)) {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] Either the leader has quit the expedition or there is no longer the minimum number of members required to continue it.")
         eim.unregisterPlayer(player)
         end(eim)
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, "[Expedition] " + player.getName() + " has left the instance.")
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

   static def isScarga(MapleMonster mob) {
      int mobid = mob.id()
      return (mobid == 9420544) || (mobid == 9420549)
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

EventScargaBattle getEvent() {
   if (!getBinding().hasVariable("event")) {
      EventManager em = (EventManager) getBinding().getVariable("em")
      getBinding().setVariable("event", new EventScargaBattle(em: em))
   }
   return (EventScargaBattle) getBinding().getVariable("event")
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