package event

import client.MapleCharacter
import net.server.world.MapleParty
import net.server.world.MaplePartyCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import server.life.MapleMonster
import server.maps.MapleMap
import server.maps.MapleReactor
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

import java.awt.*
import java.util.List

class EventCWKPQ {
   EventManager em
   boolean isPq = true
   int minPlayers = 6, maxPlayers = 30
   int minLevel = 90, maxLevel = 255
   int entryMap = 610030100
   int exitMap = 610030020
   int recruitMap = 610030020
   int clearMap = 610030020
   int minMapId = 610030100
   int maxMapId = 610030800
   int eventTime = 2
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
      List<Integer> itemSet = [4001256, 4001257, 4001258, 4001259, 4001260]
      eim.setExclusiveItems(itemSet)
   }

   // sets all possible treasures that can be given, randomly, to a player at the end of the event.
   static def setEventRewards(EventInstanceManager eim) {
      int evLevel = 1    //Rewards at clear PQ
      List<Integer> itemSet = []
      List<Integer> itemQty = []
      eim.setEventRewards(evLevel, itemSet, itemQty)

      List<Integer> expStages = [2500, 8000, 18000, 25000, 30000, 40000]    //bonus exp given on CLEAR stage signal
      eim.setEventClearStageExp(expStages)

      List<Integer> mesoStages = [500, 1000, 2000, 5000, 8000, 20000]    //bonus meso given on CLEAR stage signal
      eim.setEventClearStageMeso(mesoStages)
   }

   static def getNameFromList(int index, String[] array) {
      return array[index]
   }

   static def generateMapReactors(MapleMap map) {
      int[][] jobReactors = [[0, 0, -1, -1, 0],
                             [-1, 4, 3, 3, 3],
                             [1, 3, 4, 2, 2],
                             [2, -1, 0, 1, -1],
                             [3, 2, 1, 0, -1],
                             [4, 1, -1, 4, 1],
                             [-1, 2, 4],
                             [-1, -1]
      ]

      List<Integer> rndIndex
      def jobFound
      while (true) {
         jobFound = [:]
         rndIndex = []

         for (int i = 0; i < jobReactors.length; i++) {
            int[] jobReactorSlot = jobReactors[i]

            int idx = Math.floor(Math.random() * jobReactorSlot.length).intValue()
            jobFound["" + jobReactorSlot[idx]] = 1
            rndIndex.push(idx)
         }

         if (jobFound.keySet().size() == 6) {
            break
         }
      }

      List<String> toDeploy = []

      toDeploy.push(getNameFromList(rndIndex[0], ["4skill0a", "4skill0b", "4fake1c", "4fake1d", "4skill0e"] as String[]))
      toDeploy.push(getNameFromList(rndIndex[1], ["4fake0a", "4skill4b", "4skill3c", "4skill3d", "4skill3e"] as String[]))
      toDeploy.push(getNameFromList(rndIndex[2], ["4skill1a", "4skill3b", "4skill4c", "4skill2d", "4skill2e"] as String[]))
      toDeploy.push(getNameFromList(rndIndex[3], ["4skill2a", "4fake1b", "4skill0c", "4skill1d", "4fake1e"] as String[]))
      toDeploy.push(getNameFromList(rndIndex[4], ["4skill3a", "4skill2b", "4skill1c", "4skill0d", "4fake0e"] as String[]))
      toDeploy.push(getNameFromList(rndIndex[5], ["4skill4a", "4skill1b", "4fake0c", "4skill4d", "4skill1e"] as String[]))
      toDeploy.push(getNameFromList(rndIndex[6], ["4fake1a", "4skill2c", "4skill4e"] as String[]))
      toDeploy.push(getNameFromList(rndIndex[7], ["4fake0b", "4fake0d"] as String[]))

      List<MapleReactor> toRandomize = []

      for (int i = 0; i < toDeploy.size(); i++) {
         MapleReactor react = map.getReactorByName(toDeploy[i])

         react.setState((byte) 1)
         toRandomize.push(react)
      }

      map.shuffleReactors(toRandomize)
   }

   // selects, from the given party, the team that is allowed to attempt this event
   def getEligibleParty(MaplePartyCharacter[] party) {
   }

   // Setup the instance when invoked, EG : start PQ
   def setup(int channel) {
      EventInstanceManager eim = em.newInstance("CWKPQ" + channel)

      eim.setProperty("current_instance", "0")
      eim.setProperty("glpq1", "0")
      eim.setProperty("glpq2", "0")
      eim.setProperty("glpq3", "0")
      eim.setProperty("glpq3_p", "0")
      eim.setProperty("glpq4", "0")
      eim.setProperty("glpq5", "0")
      eim.setProperty("glpq5_room", "0")
      eim.setProperty("glpq6", "0")

      eim.setProperty("glpq_f0", "0")
      eim.setProperty("glpq_f1", "0")
      eim.setProperty("glpq_f2", "0")
      eim.setProperty("glpq_f3", "0")
      eim.setProperty("glpq_f4", "0")
      eim.setProperty("glpq_f5", "0")
      eim.setProperty("glpq_f6", "0")
      eim.setProperty("glpq_f7", "0")
      eim.setProperty("glpq_s", "0")

      int level = 1
      eim.getInstanceMap(610030100).resetPQ(level)
      eim.getInstanceMap(610030200).resetPQ(level)
      eim.getInstanceMap(610030300).resetPQ(level)
      eim.getInstanceMap(610030400).resetPQ(level)
      eim.getInstanceMap(610030500).resetPQ(level)
      eim.getInstanceMap(610030510).resetPQ(level)
      eim.getInstanceMap(610030520).resetPQ(level)
      eim.getInstanceMap(610030521).resetPQ(level)
      eim.getInstanceMap(610030522).resetPQ(level)
      eim.getInstanceMap(610030530).resetPQ(level)
      eim.getInstanceMap(610030540).resetPQ(level)
      eim.getInstanceMap(610030550).resetPQ(level)
      eim.getInstanceMap(610030600).resetPQ(level)
      eim.getInstanceMap(610030700).resetPQ(level)
      eim.getInstanceMap(610030800).resetPQ(level)

      generateMapReactors(eim.getInstanceMap(610030400))
      eim.getInstanceMap(610030550).shuffleReactors()

      //add environments
      String[] a = ["a", "b", "c", "d", "e", "f", "g", "h", "i"]
      MapleMap map = eim.getInstanceMap(610030400)
      for (int x = 0; x < a.length; x++) {
         for (int y = 1; y <= 7; y++) {
            if (x == 1 || x == 3 || x == 4 || x == 6 || x == 8) {
               if (y != 2 && y != 4 && y != 5 && y != 7) {
                  map.moveEnvironment(a[x] + "" + y, 1)
               }
            } else {
               map.moveEnvironment(a[x] + "" + y, 1)
            }
         }
      }

      int[] pos_x = [944, 401, 28, -332, -855]
      int[] pos_y = [-204, -384, -504, -384, -204]
      map = eim.getInstanceMap(610030540)
      for (int z = 0; z < pos_x.length; z++) {
         em.getMonster(9400594).ifPresent({ mob ->
            eim.registerMonster(mob)
            map.spawnMonsterOnGroundBelow(mob, new Point(pos_x[z], pos_y[z]))
         })
      }

      eim.startEventTimer(eventTime * 60000)
      setEventRewards(eim)
      setEventExclusives(eim)

      eim.schedule("spawnGuardians", 60000)
      return eim
   }

   // Happens after the event instance is initialized and all players have been assigned for the event instance, but before entering players.
   def afterSetup(EventInstanceManager eim) {
   }

   // Defines which maps inside the event are allowed to respawn. This function should create a new task at the end of it's body calling itself at a given respawn rate.
   def respawnStages(EventInstanceManager eim) {
   }

   // Warp player in etc..
   static def playerEntry(EventInstanceManager eim, MapleCharacter player) {
      MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("EXPEDITION_PLAYER_ENTER_MAP").with(player.getName()))
      MapleMap map = eim.getMapInstance(610030100 + (eim.getIntProperty("current_instance") * 100))
      player.changeMap(map, map.getPortal(0))
   }

   static def spawnGuardians(EventInstanceManager eim) {
      MapleMap map = eim.getMapInstance(610030100)
      if (map.countPlayers() <= 0) {
         return
      }
      MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.PINK_TEXT, I18nMessage.from("MASTER_GUARDIANS_DETECTED_YOU"))
      for (int i = 0; i < 20; i++) { //spawn 20 guardians
         eim.getMonster(9400594).ifPresent({ mob ->
            eim.registerMonster(mob)
            map.spawnMonsterOnGroundBelow(mob, new Point(1000, 336))
         })
      }
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

   // What to do when player've changed map, based on the mapId.
   def changedMap(EventInstanceManager eim, MapleCharacter player, int mapId) {
      if (mapId < minMapId || mapId > maxMapId) {
         if (eim.isEventTeamLackingNow(true, minPlayers, player)) {
            eim.unregisterPlayer(player)
            MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("EXPEDITION_LEADER_QUIT_OR_NOT_MINIMUM_PLAYERS"))
            end(eim)
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("EXPEDITION_PLAYER_LEFT_INSTANCE").with(player.getName()))
            eim.unregisterPlayer(player)
         }
      } else {
         switch (mapId) {
            case 610030200:
               if (eim.getIntProperty("current_instance") == 0) {
                  eim.restartEventTimer(600000) //10 mins
                  eim.setIntProperty("current_instance", 1)
               }
               break
            case 610030300:
               if (eim.getIntProperty("current_instance") == 1) {
                  eim.restartEventTimer(600000) //10 mins
                  eim.setIntProperty("current_instance", 2)
               }
               break
            case 610030400:
               if (eim.getIntProperty("current_instance") == 2) {
                  eim.restartEventTimer(600000) //10 mins
                  eim.setIntProperty("current_instance", 3)
               }
               break
            case 610030500:
               if (eim.getIntProperty("current_instance") == 3) {
                  eim.restartEventTimer(1200000) //20 mins
                  eim.setIntProperty("current_instance", 4)
               }
               break
            case 610030600:
               if (eim.getIntProperty("current_instance") == 4) {
                  eim.restartEventTimer(3600000) //1 hr
                  eim.setIntProperty("current_instance", 5)
               }
               break
            case 610030800:
               if (eim.getIntProperty("current_instance") == 5) {
                  eim.restartEventTimer(60000) //1 min
                  eim.setIntProperty("current_instance", 6)
               }
               break
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
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EVENT_TIMEOUT"))
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
   static def monsterValue(EventInstanceManager eim, int mobId) {
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
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("EXPEDITION_LEADER_QUIT_OR_NOT_MINIMUM_PLAYERS"))
         end(eim)
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("EXPEDITION_PLAYER_LEFT_INSTANCE").with(player.getName()))
         eim.unregisterPlayer(player)
      }
   }

   // return 0 - Deregister player normally + Dispose instance if there are zero player left
   // return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
   // return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
   def playerDisconnected(EventInstanceManager eim, MapleCharacter player) {
      if (eim.isEventTeamLackingNow(true, minPlayers, player)) {
         eim.unregisterPlayer(player)
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("EXPEDITION_LEADER_QUIT_OR_NOT_MINIMUM_PLAYERS"))
         end(eim)
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("EXPEDITION_PLAYER_LEFT_INSTANCE").with(player.getName()))
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
   }

   // Happens when the party is disbanded by the leader.
   def disbandParty(EventInstanceManager eim, MapleCharacter player) {
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

EventCWKPQ getEvent() {
   if (!getBinding().hasVariable("event")) {
      EventManager em = (EventManager) getBinding().getVariable("em")
      getBinding().setVariable("event", new EventCWKPQ(em: em))
   }
   return (EventCWKPQ) getBinding().getVariable("event")
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