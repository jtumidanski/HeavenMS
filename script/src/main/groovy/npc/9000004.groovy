package npc
import tools.I18nMessage

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
   String mobCount

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
         String nthText = "last"
         if (status == 0) {
            party = eim.getPlayers() as MaplePartyCharacter[]
            preamble = eim.getProperty("leader" + nthText + "preamble")
            mobCount = eim.getProperty("leader" + nthText + "mobcount")
            if (preamble == null) {
               cm.sendOk(I18nMessage.from("9000004_SHALL_WE_GET_STARTED").with(nthText))
               status = 9
            } else {
               if (!isLeader()) {
                  if (mobCount == null) {
                     cm.sendOk(I18nMessage.from("9000004_PARTY_LEADER_MUST_TALK"))
                     cm.dispose()
                  } else {
                     cm.warp(109020001, 0)
                     cm.dispose()
                  }
               }
               if (mobCount == null) {
                  cm.sendYesNo(I18nMessage.from("9000004_FINISHED"))
               }
            }
         } else if (status == 1) {
            //if (cm.mobCount(600010000)==0) {
            if (cm.countMonster() == 0) {
               cm.sendOk(I18nMessage.from("9000004_GOOD_JOB"))
            } else {
               cm.sendOk(I18nMessage.from("9000004_KILL_THOSE_CREATURES"))
               cm.dispose()
            }
         } else if (status == 2) {
            cm.sendOk(I18nMessage.from("9000004_YOU_MAY_CONTINUE"))
         } else if (status == 3) {
            cm.getMap().clearMapObjects()
            eim.setProperty("leader" + nthText + "mobcount", "done")
            MapleMap map = eim.getMapInstance(109020001)
            MapleCharacter[] members = eim.getPlayers()
            cm.warpMembers(map, members)
            cm.givePartyExp(2500, eim.getPlayers())
            cm.dispose()
         } else if (status == 10) {
            eim.setProperty("leader" + nthText + "preamble", "done")
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