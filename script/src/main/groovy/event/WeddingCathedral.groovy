package event

import client.MapleCharacter
import net.server.world.MapleParty
import net.server.world.MaplePartyCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.maps.MapleMap
import tools.MasterBroadcaster
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage
import tools.packet.wedding.WeddingProgress

import java.awt.*
import java.util.List

class EventWeddingCathedral {
   EventManager em
   boolean isPq = true
   int minPlayers, maxPlayers
   int minLevel, maxLevel
   int entryMap = 680000200
   int exitMap = 680000500
   int recruitMap = 680000000
   int clearMap = 680000500
   int minMapId = 680000100
   int maxMapId = 680000401
   int eventTime = 10
   int[] lobbyRange = [0, 0]

   int startMsgTime = 4
   int blessMsgTime = 5
   int ceremonyTime = 20
   int blessingsTime = 15
   int partyTime = 45
   int forceHideMsgTime = 10
   boolean eventBoss = true
   boolean isCathedral = true

   // After loading, ChannelServer
   def init() {
   }

   def setLobbyRange() {
      return lobbyRange
   }

   // sets requirement info about the event to be displayed at the recruitment area.
   def setEventRequirements() {
   }

   // sets all items that should exist only for the event instance, and that should be removed from inventory at the end of the run.
   static def setEventExclusives(EventInstanceManager eim) {
      List<Integer> itemSet = [4031217, 4000313]    // golden key, golden maple leaf
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

   static def spawnCakeBoss(EventInstanceManager eim) {
      MapleMap mapObj = eim.getMapInstance(680000400)
      MapleLifeFactory.getMonster(9400606).ifPresent({ mobObj -> mapObj.spawnMonsterOnGroundBelow(mobObj, new Point(777, -177)) })
   }

   // selects, from the given party, the team that is allowed to attempt this event
   def getEligibleParty(MaplePartyCharacter[] party) {
   }

   // Setup the instance when invoked, EG : start PQ
   def setup(int level, int lobbyId) {
      EventInstanceManager eim = em.newMarriage("Wedding" + lobbyId)
      eim.setProperty("weddingId", "0")
      eim.setProperty("weddingStage", "0")
      // 0: gathering time, 1: wedding time, 2: ready to fulfill the wedding, 3: just married
      eim.setProperty("guestBlessings", "0")
      eim.setProperty("isPremium", "1")
      eim.setProperty("canJoin", "1")
      eim.setProperty("groomId", "0")
      eim.setProperty("brideId", "0")
      eim.setProperty("confirmedVows", "-1")
      eim.setProperty("groomWishlist", "")
      eim.setProperty("brideWishlist", "")
      eim.initializeGiftItems()

      eim.getInstanceMap(680000400).resetPQ(level)
      if (eventBoss) {
         spawnCakeBoss(eim)
      }

      respawnStages(eim)
      eim.startEventTimer(eventTime * 60000)
      setEventRewards(eim)
      setEventExclusives(eim)
      return eim
   }

   // Happens after the event instance is initialized and all players have been assigned for the event instance, but before entering players.
   def afterSetup(EventInstanceManager eim) {
   }

   // Defines which maps inside the event are allowed to respawn. This function should create a new task at the end of it's body calling itself at a given respawn rate.
   static def respawnStages(EventInstanceManager eim) {
      eim.getMapInstance(680000400).instanceMapRespawn()
      eim.schedule("respawnStages", 15 * 1000)
   }

   // Warp player in etc..
   def playerEntry(EventInstanceManager eim, MapleCharacter player) {
      eim.setProperty("giftedItemG" + player.getId(), "0")
      eim.setProperty("giftedItemB" + player.getId(), "0")
      player.getAbstractPlayerInteraction().gainItem(4000313, (short) 1)

      MapleMap map = eim.getMapInstance(entryMap)
      player.changeMap(map, map.getPortal(0))
   }

   def stopBlessings(EventInstanceManager eim) {
      MapleMap map = eim.getMapInstance(entryMap + 10)
      MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.LIGHT_BLUE, "Wedding Assistant: Alright people, our couple are preparing their vows to each other right now.")

      eim.setIntProperty("weddingStage", 2)
   }

   static def sendWeddingAction(EventInstanceManager eim, type) {
      MapleCharacter chr = eim.getLeader()
      if (chr.getGender() == 0) {
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new WeddingProgress(type == 2, eim.getIntProperty("groomId"), eim.getIntProperty("brideId"), (byte) (type + 1)))
      } else {
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new WeddingProgress(type == 2, eim.getIntProperty("brideId"), eim.getIntProperty("groomId"), (byte) (type + 1)))
      }
   }

   static def hidePriestMsg(EventInstanceManager eim) {
      sendWeddingAction(eim, 2)
   }

   def showStartMsg(EventInstanceManager eim) {
      MasterBroadcaster.getInstance().sendToAllInMap(eim.getMapInstance(entryMap + 10), new WeddingProgress(false, 0, 0, (byte) 0))
      eim.schedule("hidePriestMsg", forceHideMsgTime * 1000)
   }

   def showBlessMsg(EventInstanceManager eim) {
      MasterBroadcaster.getInstance().sendToAllInMap(eim.getMapInstance(entryMap + 10), new WeddingProgress(false, 0, 0, (byte) 1))
      eim.setIntProperty("guestBlessings", 1)
      eim.schedule("hidePriestMsg", forceHideMsgTime * 1000)
   }

   def showMarriedMsg(EventInstanceManager eim) {
      sendWeddingAction(eim, 3)
      eim.schedule("hidePriestMsg", 10 * 1000)

      eim.restartEventTimer(partyTime * 60000)
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

   static def isMarrying(EventInstanceManager eim, MapleCharacter player) {
      int playerId = player.getId()
      return playerId == eim.getIntProperty("groomId") || playerId == eim.getIntProperty("brideId")
   }

   // What to do when player've changed map, based on the map id.
   def changedMap(EventInstanceManager eim, MapleCharacter player, int mapId) {
      if (mapId < minMapId || mapId > maxMapId) {
         if (isMarrying(eim, player)) {
            eim.unregisterPlayer(player)
            end(eim)
         } else {
            eim.unregisterPlayer(player)
         }
      }
   }

   // Do something if the party leader has been changed.
   def changedLeader(EventInstanceManager eim, MapleCharacter leader) {
   }

   // When event timeout without before completion..
   def scheduledTimeout(EventInstanceManager eim) {
      if (eim.getIntProperty("canJoin") == 1) {
         em.getChannelServer().closeOngoingWedding(isCathedral)
         eim.setIntProperty("canJoin", 0)

         MapleMap map = eim.getMapInstance(entryMap)
         MapleCharacter chr = map.getCharacterById(eim.getIntProperty("groomId"))
         if (chr != null) {
            chr.changeMap(entryMap + 10, "we00")
         }

         chr = map.getCharacterById(eim.getIntProperty("brideId"))
         if (chr != null) {
            chr.changeMap(entryMap + 10, "we00")
         }

         MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.LIGHT_BLUE, "Wedding Assistant: The couple are heading to the altar, hurry hurry talk to me to arrange your seat.")

         eim.setIntProperty("weddingStage", 1)
         eim.schedule("showStartMsg", startMsgTime * 60 * 1000)
         eim.schedule("showBlessMsg", blessMsgTime * 60 * 1000)
         eim.schedule("stopBlessings", blessingsTime * 60 * 1000)
         eim.startEventTimer(ceremonyTime * 60000)
      } else {
         end(eim)
      }
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
   static def monsterKilled(MapleMonster mob, EventInstanceManager eim) {
      if (isCakeBoss(mob)) {
         eim.showClearEffect()
         eim.clearPQ()
      }
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
      if (isMarrying(eim, player)) {
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
      if (isMarrying(eim, player)) {
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

   static def isCakeBoss(MapleMonster mob) {
      return mob.id() == 9400606
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

EventWeddingCathedral getEvent() {
   if (!getBinding().hasVariable("event")) {
      EventManager em = (EventManager) getBinding().getVariable("em")
      getBinding().setVariable("event", new EventWeddingCathedral(em: em))
   }
   return (EventWeddingCathedral) getBinding().getVariable("event")
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