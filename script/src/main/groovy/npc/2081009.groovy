package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2081009 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.dispose()
         return
      }

      status++
      if (status == 0) {
         if (cm.isQuestStarted(6180)) {
            cm.sendYesNo("Pay attention: during the time you stay inside the training ground make sure you #bhave equipped your #t1092041##k, it is of the utmost importance. Are you ready to proceed to the training area?")
         } else {
            cm.sendOk("Only assigned personnel can access the training ground.")
            cm.dispose()
         }
      } else if (status == 1) {
         cm.warp(924000001, 0)
         cm.sendOk("Have your shield equipped until the end of the quest, or else you will need to start all over again!")

         cm.resetQuestProgress(6180, 9300096)
         cm.dispose()
      }
   }
}

NPC2081009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2081009(cm: cm))
   }
   return (NPC2081009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }