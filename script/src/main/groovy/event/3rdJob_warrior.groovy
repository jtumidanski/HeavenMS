package event

import client.MapleCharacter
import net.server.world.MapleParty
import net.server.world.MaplePartyCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import server.life.MapleMonster
import tools.MaplePacketCreator
import tools.MessageBroadcaster
import tools.PacketCreator
import tools.ServerNoticeType
import tools.packet.ui.GetClock

class Event3rdJob_warrior {
   EventManager em
   boolean isPq = true
   int minPlayers, maxPlayers
   int minLevel, maxLevel
   int entryMap = 108010300
   int exitMap = 10507001
   int recruitMap
   int clearMap
   int minMapId = 108010300
   int maxMapId = 108010301
   int eventTime = 20
   int[] lobbyRange = [0, 7]

   // After loading, ChannelServer
   def init() {
      em.setProperty("noEntry", "false")
   }

   def setLobbyRange() {
      return lobbyRange
   }

   // sets requirement info about the event to be displayed at the recruitment area.
   def setEventRequirements() {
   }

   // sets all items that should exist only for the event instance, and that should be removed from inventory at the end of the run.
   def setEventExclusives(EventInstanceManager eim) {
   }

   // sets all possible treasures that can be given, randomly, to a player at the end of the event.
   def setEventRewards(EventInstanceManager eim) {
   }

   // selects, from the given party, the team that is allowed to attempt this event
   def getEligibleParty(MaplePartyCharacter[] party) {
   }

   // Setup the instance when invoked, EG : start PQ
   def setup(int level, int lobbyid) {
      EventInstanceManager eim = em.newInstance("3rdJob_warrior_" + lobbyid)
      eim.setProperty("level", level)
      eim.setProperty("boss", "0")

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
      eim.getInstanceMap(maxMapId).resetPQ(1)

      player.changeMap(entryMap, 0)
      em.setProperty("noEntry", "true")

      PacketCreator.announce(player, new GetClock(eventTime * 60))
      eim.startEventTimer(eventTime * 60000)
   }

   // Do something with the player that is about to unregister right before unregistering he/she.
   def playerUnregistered(EventInstanceManager eim, MapleCharacter player) {
   }

   // Do something with the player right before disbanding the event instance.
   def playerExit(EventInstanceManager eim, MapleCharacter player) {
      eim.unregisterPlayer(player)
      eim.dispose()
      em.setProperty("noEntry", "false")
   }

   // Do something with the player right before leaving the party.
   def playerLeft(EventInstanceManager eim, MapleCharacter player) {
   }

   // What to do when player've changed map, based on the mapid.
   def changedMap(EventInstanceManager eim, MapleCharacter player, int mapid) {
      if (mapid < minMapId || mapid > maxMapId) {
         playerExit(eim, player)
      }
   }

   // Do something if the party leader has been changed.
   def changedLeader(EventInstanceManager eim, MapleCharacter leader) {
   }

   // When event timeout without before completion..
   def scheduledTimeout(EventInstanceManager eim) {
      MapleCharacter player = eim.getPlayers().get(0)
      playerExit(eim, eim.getPlayers().get(0))
      player.changeMap(exitMap)
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
   }

   // Happens when an opposing mob revives
   def monsterRevive(mob, EventInstanceManager eim) {
   }

   // Happens when player's revived.
   // @Param : returns true/false
   def playerRevive(EventInstanceManager eim, player) {
   }

   // return 0 - Deregister player normally + Dispose instance if there are zero player left
   // return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
   // return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
   def playerDisconnected(EventInstanceManager eim, MapleCharacter player) {
      playerExit(eim, player)
   }

   // Happens when the party fails to complete the event instance.
   def end(EventInstanceManager eim) {
   }

   // Selects randomly a reward to give from the reward pool.
   def giveRandomEventReward(EventInstanceManager eim, MapleCharacter player) {
   }

   // Happens when the party succeeds on completing the event instance.
   def clearPQ(EventInstanceManager eim) {
      MapleCharacter player = eim.getPlayers().get(0)
      eim.unregisterPlayer(player)
      player.changeMap(exitMap)

      eim.dispose()
      em.setProperty("noEntry", "false")
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

Event3rdJob_warrior getEvent() {
   if (!getBinding().hasVariable("event")) {
      EventManager em = (EventManager) getBinding().getVariable("em")
      getBinding().setVariable("event", new Event3rdJob_warrior(em: em))
   }
   return (Event3rdJob_warrior) getBinding().getVariable("event")
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