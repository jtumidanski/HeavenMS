package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9060000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   boolean completed

   def start() {
      completed = cm.haveItem(4031508, 5) && cm.haveItem(4031507, 5)

      if (completed) {
         cm.sendNext("Wow~ You have succeeded in collecting 5 of each #b#t4031508##k and #b#t4031507##k. Okay then, I will send you to Zoo. Please talk to me again when you get there.")
      } else {
         cm.sendYesNo("You haven't completed the requirements. Are you sure you want to leave?")
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         cm.dispose()
         return
      }

      if (status == 0) {
         cm.sendOk("Well okay, I will send you back.")
      } else {
         if (completed) {
            cm.getEventInstance().clearPQ()
         } else {
            cm.warp(923010100, 0)
         }

         cm.dispose()
      }
   }
}

NPC9060000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9060000(cm: cm))
   }
   return (NPC9060000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }