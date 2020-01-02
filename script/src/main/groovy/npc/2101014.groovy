package npc

import client.MapleCharacter
import scripting.npc.NPCConversationManager
import server.expeditions.MapleExpedition
import server.expeditions.MapleExpeditionType
import server.maps.MapleMapManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2101014 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int arenaType
   int map
   MapleExpeditionType expeditionType = MapleExpeditionType.ARIANT
   MapleExpeditionType expeditionType1 = MapleExpeditionType.ARIANT1
   MapleExpeditionType expeditionType2 = MapleExpeditionType.ARIANT2

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (cm.getPlayer().getMapId() == 980010000) {
            if (cm.getLevel() > 30) {
               cm.sendOk("You are already over #rlevel 30#k, therefore you can't participate in this instance anymore.")
               cm.dispose()
               return
            }

            if (status == 0) {
               MapleExpedition expedition = cm.getExpedition(expeditionType)
               MapleExpedition expedition1 = cm.getExpedition(expeditionType1)
               MapleExpedition expedition2 = cm.getExpedition(expeditionType2)

               MapleMapManager channelMaps = cm.getClient().getChannelServer().getMapFactory()
               String startSnd = "What would you like to do? \r\n\r\n\t#e#r(Choose a Battle Arena)#n#k\r\n#b"
               String toSnd = startSnd

               if (expedition == null) {
                  toSnd += "#L0#Battle Arena (1) (Empty)#l\r\n"
               } else if (channelMaps.getMap(980010101).getCharacters().isEmpty()) {
                  toSnd += "#L0#Join Battle Arena (1)  Owner (" + expedition.getLeader().getName() + ")" + " Current Member: " + cm.getExpeditionMemberNames(expeditionType) + "\r\n"
               }
               if (expedition1 == null) {
                  toSnd += "#L1#Battle Arena (2) (Empty)#l\r\n"
               } else if (channelMaps.getMap(980010201).getCharacters().isEmpty()) {
                  toSnd += "#L1#Join Battle Arena (2)  Owner (" + expedition1.getLeader().getName() + ")" + " Current Member: " + cm.getExpeditionMemberNames(expeditionType1) + "\r\n"
               }
               if (expedition2 == null) {
                  toSnd += "#L2#Battle Arena (3) (Empty)#l\r\n"
               } else if (channelMaps.getMap(980010301).getCharacters().isEmpty()) {
                  toSnd += "#L2#Join Battle Arena (3)  Owner (" + expedition2.getLeader().getName() + ")" + " Current Member: " + cm.getExpeditionMemberNames(expeditionType2) + "\r\n"
               }
               if (toSnd == startSnd) {
                  cm.sendOk("All the Battle Arena is currently occupied. I suggest you to come back later or change channels.")
                  cm.dispose()
               } else {
                  cm.sendSimple(toSnd)
               }
            } else if (status == 1) {
               arenaType = selection
               MapleExpedition expedition = fetchArenaType()
               if (expedition == null) {
                  cm.dispose()
                  return
               }

               if (expedition != null) {
                  enterArena(-1)
               } else {
                  cm.sendGetText("Up to how many participants can join in this match? (2~5 people)")
               }
            } else if (status == 2) {
               Integer players = (cm.getText()).toInteger()
               if (players == null) {
                  cm.sendNext("Please enter a numeric limit value of allowed players in your instance.")
                  status = 0
               } else if (players < 2) {
                  cm.sendNext("The numeric limit value should not be less than 2 players.")
                  status = 0
               } else {
                  enterArena(players)
               }
            }
         }
      }
   }


   def fetchArenaType() {
      MapleExpedition expedition
      switch (arenaType) {
         case 0:
            expeditionType = MapleExpeditionType.ARIANT
            expedition = cm.getExpedition(expeditionType)
            map = 980010100
            break
         case 1:
            expeditionType = MapleExpeditionType.ARIANT1
            expedition = cm.getExpedition(expeditionType)
            map = 980010200
            break
         case 2:
            expeditionType = MapleExpeditionType.ARIANT2
            expedition = cm.getExpedition(expeditionType)
            map = 980010300
            break
         default:
            expeditionType = null
            map = 0
            expedition = null
      }

      return expedition
   }

   def enterArena(int arenaPlayers) {
      MapleExpedition expedition = fetchArenaType()
      if (expedition == null) {
         cm.dispose()
      } else if (expedition == null) {
         if (arenaPlayers != -1) {
            int res = cm.createExpedition(expeditionType, true, 0, arenaPlayers)
            if (res == 0) {
               cm.warp(map, 0)
               MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.NOTICE, "Your arena was created successfully. Wait for people to join the battle.")
            } else if (res > 0) {
               cm.sendOk("Sorry, you've already reached the quota of attempts for this expedition! Try again another day...")
            } else {
               cm.sendOk("An unexpected error has occurred when starting the expedition, please try again later.")
            }
         } else {
            cm.sendOk("An unexpected error has occurred when locating the expedition, please try again later.")
         }

         cm.dispose()
      } else {
         if (playerAlreadyInLobby(cm.getPlayer())) {
            cm.sendOk("Sorry, you're already inside the lobby.")
            cm.dispose()
            return
         }

         int playerAdd = expedition.addMemberInt(cm.getPlayer())
         if (playerAdd == 3) {
            cm.sendOk("Sorry, the lobby is full now")
            cm.dispose()
         } else {
            if (playerAdd == 0) {
               cm.warp(map, 0)
               cm.dispose()
            } else if (playerAdd == 2) {
               cm.sendOk("Sorry, the leader do not allowed you to enter.")
               cm.dispose()
            } else {
               cm.sendOk("Error.")
               cm.dispose()
            }
         }
      }
   }

   def playerAlreadyInLobby(MapleCharacter player) {
      return cm.getExpedition(MapleExpeditionType.ARIANT) != null && cm.getExpedition(MapleExpeditionType.ARIANT).contains(player) ||
            cm.getExpedition(MapleExpeditionType.ARIANT1) != null && cm.getExpedition(MapleExpeditionType.ARIANT1).contains(player) ||
            cm.getExpedition(MapleExpeditionType.ARIANT2) != null && cm.getExpedition(MapleExpeditionType.ARIANT2).contains(player)
   }
}

NPC2101014 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2101014(cm: cm))
   }
   return (NPC2101014) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }