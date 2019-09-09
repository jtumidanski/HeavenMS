package npc

import client.MapleCharacter
import net.server.world.MaplePartyCharacter
import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.maps.MapleMap

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000004 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   MaplePartyCharacter[] party
   String preamble
   String mobcount

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         EventInstanceManager eim = cm.getPlayer().getEventInstance()
         String nthtext = "last"
         if (status == 0) {
            party = eim.getPlayers() as MaplePartyCharacter[]
            preamble = eim.getProperty("leader" + nthtext + "preamble")
            mobcount = eim.getProperty("leader" + nthtext + "mobcount")
            if (preamble == null) {
               cm.sendOk("Hi. Welcome to the " + nthtext + " stage. This is where you fight the #bboss#k. Shall we get started?")
               status = 9
            } else {
               if (!isLeader()) {
                  if (mobcount == null) {
                     cm.sendOk("Please tell your #bParty-Leader#k to come talk to me")
                     cm.dispose()
                  } else {
                     cm.warp(109020001, 0)
                     cm.dispose()
                  }
               }
               if (mobcount == null) {
                  cm.sendYesNo("You're finished?!")
               }
            }
         } else if (status == 1) {
            //if (cm.mobCount(600010000)==0) {
            if (cm.countMonster() == 0) {
               cm.sendOk("Good job! You've killed 'em!")
            } else {
               cm.sendOk("What are you talking about? Kill those creatures!!")
               cm.dispose()
            }
         } else if (status == 2) {
            cm.sendOk("You may continue to the next stage!")
         } else if (status == 3) {
            cm.getMap().clearMapObjects()
            eim.setProperty("leader" + nthtext + "mobcount", "done")
            MapleMap map = eim.getMapInstance(109020001)
            MapleCharacter[] members = eim.getPlayers()
            cm.warpMembers(map, members)
            cm.givePartyExp(2500, eim.getPlayers())
            cm.dispose()
         } else if (status == 10) {
            eim.setProperty("leader" + nthtext + "preamble", "done")
//            cm.summonMobAtPosition(8220000,25000000,1500000,1,-762,-1307);
//            cm.summonMobAtPosition(8220001,15000000,750000,1,-788,-851);
//            cm.summonMobAtPosition(9410015,15000000,750000,1,128,-851);
            cm.dispose()
         }
      }
   }

   def isLeader() {
      if (cm.getParty() == null) {
         return false
      } else {
         return cm.isLeader()
      }
   }
}

NPC9000004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000004(cm: cm))
   }
   return (NPC9000004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }