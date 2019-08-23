package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1072004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   boolean completed

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (cm.haveItem(4031013, 30)) {
               completed = true
               cm.sendNext("Ohhhhh.. you collected all 30 Dark Marbles!! It should have been difficult... just incredible! Alright. You've passed the test and for that, I'll reward you #bThe Proof of a Hero#k. Take that and go back to Perion.")
            } else {
               completed = false
               cm.sendSimple("You will have to collect me #b30 #t4031013##k. Good luck. \r\n#b#L1#I would like to leave#l")
            }
         } else if (status == 1) {
            if (completed) {
               cm.removeAll(4031013)
               cm.completeQuest(100004)
               cm.startQuest(100005)
               cm.gainItem(4031012)
            }

            cm.warp(102020300, 2)
            cm.dispose()
         }
      }
   }
}

NPC1072004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1072004(cm: cm))
   }
   return (NPC1072004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }