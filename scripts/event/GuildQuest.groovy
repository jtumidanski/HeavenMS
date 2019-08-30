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

class EventGuildQuest {
   EventManager em
   boolean isPq = true
   int minPlayers = 6, maxPlayers = 30
   int minLevel = 1, maxLevel = 255
   int entryMap = 990000000
   int exitMap = 990001100
   int recruitMap = 101030104
   int clearMap = 990001000
   int minMapId = 990000000
   int maxMapId = 990001101
   int eventTime = 90
   int[] lobbyRange = [0, 0]

   int waitTime = 3
   double bonusTime = 0.5

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

      reqStr += "\r\n    All members of the same guild"

      reqStr += "\r\n    Time limit: "
      reqStr += eventTime + " minutes"

      em.setProperty("party", reqStr)
   }

   // sets all items that should exist only for the event instance, and that should be removed from inventory at the end of the run.
   static def setEventExclusives(EventInstanceManager eim) {
      List<Integer> itemSet = [1032033, 4001024, 4001025, 4001026, 4001027, 4001028, 4001029, 4001030, 4001031, 4001032, 4001033, 4001034, 4001035, 4001037]
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
   }

   // selects, from the given party, the team that is allowed to attempt this event
   def getEligibleParty(MaplePartyCharacter[] party) {
      MaplePartyCharacter[] eligible = []
      boolean hasLeader = false

      int guildId = 0

      if (party.size() > 0) {
         MaplePartyCharacter[] partyList = party

         for (int i = 0; i < party.size(); i++) {
            MaplePartyCharacter ch = partyList[i]
            if (ch.isLeader()) {
               guildId = ch.getGuildId()
               break
            }
         }

         for (int i = 0; i < party.size(); i++) {
            MaplePartyCharacter ch = partyList[i]

            if (ch.getMapId() == recruitMap && ch.getLevel() >= minLevel && ch.getLevel() <= maxLevel && ch.getGuildId() == guildId) {
               if (ch.isLeader()) {
                  hasLeader = true
               }
               eligible << ch
            }
         }
      }

      if (!(hasLeader)) {
         eligible = []
      }
      return eligible
   }

   // Setup the instance when invoked, EG : start PQ
   def setup(int level, int lobbyid) {
      EventInstanceManager eim = em.newInstance("Guild" + lobbyid)
      eim.setProperty("level", level)

      eim.setProperty("guild", 0)
      eim.setProperty("canJoin", 1)
      eim.setProperty("canRevive", 0)

      eim.getInstanceMap(990000000).resetPQ(level)
      eim.getInstanceMap(990000100).resetPQ(level)
      eim.getInstanceMap(990000200).resetPQ(level)
      eim.getInstanceMap(990000300).resetPQ(level)
      eim.getInstanceMap(990000301).resetPQ(level)
      eim.getInstanceMap(990000400).resetPQ(level)
      eim.getInstanceMap(990000401).resetPQ(level)
      eim.getInstanceMap(990000410).resetPQ(level)
      eim.getInstanceMap(990000420).resetPQ(level)
      eim.getInstanceMap(990000430).resetPQ(level)
      eim.getInstanceMap(990000431).resetPQ(level)
      eim.getInstanceMap(990000440).resetPQ(level)
      eim.getInstanceMap(990000500).resetPQ(level)
      eim.getInstanceMap(990000501).resetPQ(level)
      eim.getInstanceMap(990000502).resetPQ(level)
      eim.getInstanceMap(990000600).resetPQ(level)
      eim.getInstanceMap(990000610).resetPQ(level)
      eim.getInstanceMap(990000611).resetPQ(level)
      eim.getInstanceMap(990000620).resetPQ(level)
      eim.getInstanceMap(990000630).resetPQ(level)
      eim.getInstanceMap(990000631).resetPQ(level)
      eim.getInstanceMap(990000640).resetPQ(level)
      eim.getInstanceMap(990000641).resetPQ(level)
      eim.getInstanceMap(990000700).resetPQ(level)
      eim.getInstanceMap(990000800).resetPQ(level)
      eim.getInstanceMap(990000900).resetPQ(level)
      eim.getInstanceMap(990001000).resetPQ(level)
      eim.getInstanceMap(990001100).resetPQ(level)
      eim.getInstanceMap(990001101).resetPQ(level)

      respawnStages(eim)

      long ts = System.currentTimeMillis()
      ts += (60000 * waitTime)
      eim.setProperty("entryTimestamp", "" + ts)

      eim.startEventTimer(waitTime * 60000)

      setEventRewards(eim)
      setEventExclusives(eim)

      return eim
   }

   static def isTeamAllJobs(EventInstanceManager eim) {
      int eventJobs = eim.getEventPlayersJobs()
      int rangeJobs = '111110'.toInteger()

      return ((eventJobs & rangeJobs) == rangeJobs)
   }

   // Happens after the event instance is initialized and all players have been assigned for the event instance, but before entrying players.
   def afterSetup(EventInstanceManager eim) {
      MapleCharacter leader = em.getChannelServer().getPlayerStorage().getCharacterById(eim.getLeaderId()).get()
      if (leader != null) {
         eim.setProperty("guild", "" + leader.getGuildId())
      }
   }

   // Defines which maps inside the event are allowed to respawn. This function should create a new task at the end of it's body calling itself at a given respawn rate.
   def respawnStages(EventInstanceManager eim) {
   }

   // Warp player in etc..
   def playerEntry(EventInstanceManager eim, MapleCharacter player) {
      MapleMap map = eim.getMapInstance(entryMap)
      player.changeMap(map, map.getPortal(0))
   }

   // Do something with the player that is about to unregister right before unregistering he/she.
   static def playerUnregistered(EventInstanceManager eim, MapleCharacter player) {
      player.cancelEffect(2023000)
      player.cancelEffect(2023001)
      player.cancelEffect(2023002)
      player.cancelEffect(2023003)
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
         if (eim.isEventTeamLackingNow(true, minPlayers, player) && eim.getIntProperty("canJoin") == 0) {
            eim.unregisterPlayer(player)
            end(eim)
         } else {
            eim.unregisterPlayer(player)
         }
      }
   }

   static def afterChangedMap(EventInstanceManager eim, MapleCharacter player, int mapid) {
      if (mapid == 990000100) {
         String texttt = "So, here is the brief. You guys should be warned that, once out on the fortress outskirts, anyone that would not be equipping the #b#t1032033##k will die instantly due to the deteriorated state of the air around there. That being said, once your team moves out, make sure to #bhit the glowing rocks#k in that region and #bequip the dropped item#k before advancing stages. That will protect you thoroughly from the air sickness. Good luck!"
         player.getAbstractPlayerInteraction().npcTalk(9040000, texttt)
      }
   }

   // Do something if the party leader has been changed.
   def changedLeader(EventInstanceManager eim, MapleCharacter leader) {
   }

   // When event timeout without before completion..
   def scheduledTimeout(EventInstanceManager eim) {
      if (eim.isEventCleared()) {
         eim.warpEventTeam(990001100)
      } else {
         if (eim.getIntProperty("canJoin") == 1) {
            eim.setProperty("canJoin", 0)

            if (eim.checkEventTeamLacking(true, minPlayers)) {
               end(eim)
            } else {
               eim.startEventTimer(eventTime * 60000)

               if (isTeamAllJobs(eim)) {
                  int rnd = Math.floor(Math.random() * 4).intValue()
                  eim.applyEventPlayersItemBuff(2023000 + rnd)
               }
            }
         } else {
            end(eim)
         }
      }
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
   def monsterKilled(MapleMonster mob, EventInstanceManager eim) {
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
      if (player.getMapId() == 990000900) {
         if (player.getMap().countAlivePlayers() == 0 && player.getMap().countMonsters() > 0) {
            end(eim)
         }
      }
   }

   // Happens when an opposing mob revives
   def monsterRevive(mob, EventInstanceManager eim) {
   }

   // Happens when player's revived.
   // @Param : returns true/false
   def playerRevive(EventInstanceManager eim, MapleCharacter player) {
      if (eim.getIntProperty("canRevive") == 0) {
         if (eim.isEventTeamLackingNow(true, minPlayers, player) && eim.getIntProperty("canJoin") == 0) {
            player.respawn(eim, exitMap)
            end(eim)
         } else {
            player.respawn(eim, exitMap)
         }

         return false
      }

      return true
   }

   // return 0 - Deregister player normally + Dispose instance if there are zero player left
   // return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
   // return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
   def playerDisconnected(EventInstanceManager eim, MapleCharacter player) {
      if (eim.isEventTeamLackingNow(true, minPlayers, player) && eim.getIntProperty("canJoin") == 0) {
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
   def clearPQ(EventInstanceManager eim) {
      eim.stopEventTimer()
      eim.setEventCleared()

      eim.warpEventTeam(clearMap)
      eim.startEventTimer((bonusTime * 60000).longValue())
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

   def reopenGuildQuest() {
      em.attemptStartGuildInstance()
   }

   // Finishes the event instance.
   def dispose() {
      long delay = (em.getLobbyDelay() * 1.5 * 1000).longValue()
      em.schedule("reopenGuildQuest", delay)
   }
}

EventGuildQuest getEvent() {
   if (!getBinding().hasVariable("event")) {
      EventManager em = (EventManager) getBinding().getVariable("em")
      getBinding().setVariable("event", new EventGuildQuest(em: em))
   }
   return (EventGuildQuest) getBinding().getVariable("event")
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

def reopenGuildQuest(EventInstanceManager eim) {
   getEvent().reopenGuildQuest()
}