package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040028 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

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

         if (status == 0) {
            String greeting = "Thank you for finding the pendulum. Are you ready to return to Eos Tower?"
            if (cm.isQuestStarted(3230)) {
               if (cm.haveItem(4031094)) {
                  cm.completeQuest(3230)
                  cm.gainItem(4031094, (short) -1)
               } else {
                  greeting = "You haven't found the pendulum yet. Do you want to go back to Eos Tower?"
               }
            }
            cm.sendYesNo(greeting)
         } else if (status == 1) {
            cm.warp(221024400, 4)
            cm.dispose()
         }
      }
   }
}

NPC2040028 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040028(cm: cm))
   }
   return (NPC2040028) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }