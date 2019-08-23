package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Olson the Toy Soldier
	Map(s): 		
	Description: 	
*/


class NPC2040002 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   EventManager em

   def start() {
      if (cm.isQuestStarted(3230)) {
         em = cm.getEventManager("DollHouse")

         if (em.getProperty("noEntry") == "false") {
            cm.sendNext("The pendulum is hidden inside a dollhouse that looks different than the others.")
         } else {
            cm.sendOk("Someone else is already searching the area. Please wait until the area is cleared.")
            cm.dispose()
         }
      } else {
         cm.sendOk("We are not allowed to let the general public wander past this point.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            cm.sendYesNo("Are you ready to enter the dollhouse map?")
         } else if (status == 2) {
            em = cm.getEventManager("DollHouse")
            if (!em.startInstance(cm.getPlayer())) {
               cm.sendOk("Hmm... The DollHouse is being challenged already, it seems. Try again later.")
            }

            cm.dispose()
         }
      }
   }
}

NPC2040002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040002(cm: cm))
   }
   return (NPC2040002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }