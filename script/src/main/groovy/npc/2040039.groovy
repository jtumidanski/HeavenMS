package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040039 {
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
                  cm.sendOk("Hi. Welcome to the #bstage " + stage + "#k. On this stage, several creatures are hiding in the shadows of the inner parts of this tower. Some of them cannot be beaten by physical means, so magical attacks are required to do the job, whereas others work the other way around. Get me 6 #t4001022#'s this time.")
                  eim.setProperty("statusStg" + stage, 0)
               } else {                      // check stage completion
                  if (cm.haveItem(4001022, 6)) {
                     cm.sendOk("Good job! You have collected all 6 #b#t4001022#'s.#k")
                     cm.gainItem(4001022, (short) -6)

                     eim.setProperty("statusStg" + stage, 1)
                     clearStage(stage, eim, curMap)
                  } else {
                     cm.sendNext("Sorry you don't have all 6 #b#t4001022#'s.#k")
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

NPC2040039 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040039(cm: cm))
   }
   return (NPC2040039) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }