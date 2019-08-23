package event

import client.MapleCharacter
import net.server.world.MapleParty
import net.server.world.MaplePartyCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.maps.MapleMap
import tools.MaplePacketCreator

import java.awt.*

class EventBoats {
   EventManager em
   boolean isPq = true
   int minPlayers, maxPlayers
   int minLevel, maxLevel
   int entryMap
   int exitMap
   int recruitMap
   int clearMap
   int minMapId
   int maxMapId
   int eventTime
   int[] lobbyRange = [0, 0]

   MapleMap Orbis_btf
   MapleMap Boat_to_Orbis
   MapleMap Orbis_Boat_Cabin
   MapleMap Orbis_Station
   MapleMap Orbis_docked
   MapleMap Boat_to_Ellinia
   MapleMap Ellinia_btf
   MapleMap Ellinia_Boat_Cabin
   MapleMap Ellinia_docked

//Time Setting is in millisecond
   int closeTime = 4 * 60 * 1000 //The time to close the gate
   int beginTime = 5 * 60 * 1000 //The time to begin the ride
   int rideTime = 10 * 60 * 1000 //The time that require move to destination
   int invasionStartTime = 3 * 60 * 1000 //The time to balrog ship approach
   int invasionDelayTime = 1 * 60 * 1000 //The time to balrog ship approach
   int invasionDelay = 5 * 1000 //The time that spawn balrog

   // After loading, ChannelServer
   def init() {
      closeTime = em.getTransportationTime(closeTime)
      beginTime = em.getTransportationTime(beginTime)
      rideTime = em.getTransportationTime(rideTime)
      invasionStartTime = em.getTransportationTime(invasionStartTime)
      invasionDelayTime = em.getTransportationTime(invasionDelayTime)

      Orbis_btf = em.getChannelServer().getMapFactory().getMap(200000112)
      Ellinia_btf = em.getChannelServer().getMapFactory().getMap(101000301)
      Boat_to_Orbis = em.getChannelServer().getMapFactory().getMap(200090010)
      Boat_to_Ellinia = em.getChannelServer().getMapFactory().getMap(200090000)
      Orbis_Boat_Cabin = em.getChannelServer().getMapFactory().getMap(200090011)
      Ellinia_Boat_Cabin = em.getChannelServer().getMapFactory().getMap(200090001)
      Ellinia_docked = em.getChannelServer().getMapFactory().getMap(101000300)
      Orbis_Station = em.getChannelServer().getMapFactory().getMap(200000100)
      Orbis_docked = em.getChannelServer().getMapFactory().getMap(200000111)

      Ellinia_docked.setDocked(true)
      Orbis_docked.setDocked(true)

      scheduleNew()
   }

   def scheduleNew() {
      em.setProperty("docked", "true")

      em.setProperty("entry", "true")
      em.setProperty("haveBalrog", "false")
      em.schedule("stopentry", closeTime)
      em.schedule("takeoff", beginTime)
   }

   def stopentry() {
      em.setProperty("entry", "false")
      Orbis_Boat_Cabin.clearMapObjects()   //boxes
      Ellinia_Boat_Cabin.clearMapObjects()
   }

   def takeoff() {
      Orbis_btf.warpEveryone(Boat_to_Ellinia.getId())
      Ellinia_btf.warpEveryone(Boat_to_Orbis.getId())
      Ellinia_docked.broadcastShip(false)
      Orbis_docked.broadcastShip(false)

      em.setProperty("docked", "false")

      long delay = (invasionStartTime + (Math.random() * invasionDelayTime)).longValue()
      if (Math.random() < 0.42) {
         em.schedule("approach", delay)
      }
      em.schedule("arrived", rideTime)
   }

   def arrived() {
      Boat_to_Orbis.warpEveryone(Orbis_Station.getId(), 0)
      Orbis_Boat_Cabin.warpEveryone(Orbis_Station.getId(), 0)
      Boat_to_Ellinia.warpEveryone(Ellinia_docked.getId(), 1)
      Ellinia_Boat_Cabin.warpEveryone(Ellinia_docked.getId(), 1)
      Orbis_docked.broadcastShip(true)
      Ellinia_docked.broadcastShip(true)
      Boat_to_Orbis.broadcastEnemyShip(false)
      Boat_to_Ellinia.broadcastEnemyShip(false)
      Boat_to_Orbis.killAllMonsters()
      Boat_to_Ellinia.killAllMonsters()
      em.setProperty("haveBalrog", "false")
      scheduleNew()
   }

   def approach() {
      if (Math.floor(Math.random() * 10) < 10) {
         em.setProperty("haveBalrog", "true")
         Boat_to_Orbis.broadcastEnemyShip(true)
         Boat_to_Ellinia.broadcastEnemyShip(true)
         Boat_to_Orbis.broadcastMessage(MaplePacketCreator.musicChange("Bgm04/ArabPirate"))
         Boat_to_Ellinia.broadcastMessage(MaplePacketCreator.musicChange("Bgm04/ArabPirate"))

         em.schedule("invasion", invasionDelay)
      }
   }

   def invasion() {
      MapleMap map1 = Boat_to_Ellinia
      Point pos1 = new Point(-538, 143)
      map1.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8150000), pos1)
      map1.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8150000), pos1)

      MapleMap map2 = Boat_to_Orbis
      Point pos2 = new Point(339, 148)
      map2.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8150000), pos2)
      map2.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8150000), pos2)
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
   }

   // Happens after the event instance is initialized and all players have been assigned for the event instance, but before entrying players.
   def afterSetup(EventInstanceManager eim) {
   }

   // Defines which maps inside the event are allowed to respawn. This function should create a new task at the end of it's body calling itself at a given respawn rate.
   def respawnStages(EventInstanceManager eim) {
   }

   // Warp player in etc..
   def playerEntry(EventInstanceManager eim, MapleCharacter player) {
   }

   // Do something with the player that is about to unregister right before unregistering he/she.
   def playerUnregistered(EventInstanceManager eim, MapleCharacter player) {
   }

   // Do something with the player right before disbanding the event instance.
   def playerExit(EventInstanceManager eim, MapleCharacter player) {
   }

   // Do something with the player right before leaving the party.
   def playerLeft(EventInstanceManager eim, MapleCharacter player) {
   }

   // What to do when player've changed map, based on the mapid.
   def changedMap(EventInstanceManager eim, MapleCharacter player, int mapid) {
   }

   // Do something if the party leader has been changed.
   def changedLeader(EventInstanceManager eim, MapleCharacter leader) {
   }

   // When event timeout without before completion..
   def scheduledTimeout(EventInstanceManager eim) {
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
   def monsterKilled(MapleMonster mob, EventInstanceManager eim) {
   }

   // Invoked when a monster that's registered has been killed
   // return x amount for this player - "Saved Points"
   static def monsterValue(EventInstanceManager eim, int mobid) {
      return 0
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
   }

   // return 0 - Deregister player normally + Dispose instance if there are zero player left
   // return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
   // return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
   def playerDisconnected(EventInstanceManager eim, MapleCharacter player) {
   }

   // Happens when the party fails to complete the event instance.
   def end(EventInstanceManager eim) {
   }

   // Selects randomly a reward to give from the reward pool.
   def giveRandomEventReward(EventInstanceManager eim, MapleCharacter player) {
   }

   // Happens when the party succeeds on completing the event instance.
   def clearPQ(EventInstanceManager eim) {
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

EventBoats getEvent() {
   if (!getBinding().hasVariable("event")) {
      EventManager em = (EventManager) getBinding().getVariable("em")
      getBinding().setVariable("event", new EventBoats(em: em))
   }
   return (EventBoats) getBinding().getVariable("event")
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

def stopentry(EventInstanceManager eim) {
   getEvent().stopentry()
}

def takeoff(EventInstanceManager eim) {
   getEvent().takeoff()
}

def approach(EventInstanceManager eim) {
   getEvent().approach()
}

def arrived(EventInstanceManager eim) {
   getEvent().arrived()
}

def invasion(EventInstanceManager eim) {
   getEvent().invasion()
}