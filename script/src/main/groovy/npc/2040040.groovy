package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040040 {
   NPCConversationManager cm
   int status = 0
   int sel = -1
   int curMap, stage

   def start() {
      curMap = cm.getMapId()
      stage = Math.floor((curMap - 922010100) / 100).intValue() + 1
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   static def clearStage(int stage, EventInstanceManager eim, int curMap) {
      eim.setProperty(stage + "stageclear", "true")
      eim.showClearEffect(true)

      eim.linkToNextStage(stage, "lpq", curMap)  //opens the portal to the next map
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

         if (eim.getProperty(stage.toString() + "stageclear") != null) {
            cm.sendNext("Hurry, goto the next stage, the portal is open!")
         } else {
            if (eim.isEventLeader(cm.getPlayer())) {
               int state = eim.getIntProperty("statusStg" + stage)

               if (state == -1) {           // preamble
                  cm.sendOk("Hi. Welcome to the #bstage " + stage + "#k. This is the 2nd stage, but everyone has to cooperate. There are 6 portals here. One is guarded by monsters which can not be defeated, and one is very high. I'd like you and your party to go in each one and break the boxes inside. Bring back the drops -- there should be 24.")
                  eim.setProperty("statusStg" + stage, 0)
               } else {       // check stage completion
                  if (cm.haveItem(4001022, 24)) {
                     cm.sendOk("Good job! You have collected all 24 #b#t4001022#'s.#k")
                     cm.gainItem(4001022, (short) -24)

                     eim.setProperty("statusStg" + stage, 1)
                     clearStage(stage, eim, curMap)
                  } else {
                     cm.sendNext("Sorry you don't have all 24 #b#t4001022#'s.#k")
                  }
               }
            } else {
               cm.sendNext("Please tell your #bParty-Leader#k to come talk to me.")
            }
         }

         cm.dispose()
      }
   }
}

NPC2040040 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040040(cm: cm))
   }
   return (NPC2040040) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }