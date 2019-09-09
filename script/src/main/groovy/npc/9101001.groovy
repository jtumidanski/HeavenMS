package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9101001 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      cm.sendNext("You have finished all your trainings. Good job. You seem to be ready to start with the journey right away! Good, I will let you move on to the next place.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            cm.sendNextPrev("But remember, once you get out of here, you will enter a village full with monsters. Well them, good bye!")
         } else if (status == 2) {
            cm.warp(40000, 0)
            cm.gainExp(3)
            cm.dispose()
         }
      }
   }
}

NPC9101001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9101001(cm: cm))
   }
   return (NPC9101001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }