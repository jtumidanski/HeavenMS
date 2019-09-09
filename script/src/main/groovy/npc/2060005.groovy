package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2060005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.isQuestCompleted(6002)) {
         cm.sendOk("Thanks for saving the pork.")
      } else if (cm.isQuestStarted(6002)) {
         if (cm.haveItem(4031507, 5) && cm.haveItem(4031508, 5)) {
            cm.sendOk("Thanks for saving the pork.")
         } else {
            EventManager em = cm.getEventManager("3rdJob_mount")
            if (em == null) {
               cm.sendOk("Sorry, but 3rd job advancement (mount) is closed.")
            } else {
               if (em.startInstance(cm.getPlayer())) {
                  cm.removeAll(4031507)
                  cm.removeAll(4031508)
               } else {
                  cm.sendOk("There is currently someone in this map, come back later.")
               }
            }
         }
      } else {
         cm.sendOk("Only few adventurers, from a selected public, are eligible to protect the Watch Hog.")
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2060005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2060005(cm: cm))
   }
   return (NPC2060005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }