package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9103000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int qty = 0

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
            if (cm.isEventLeader()) {
               if (!cm.getEventInstance().isEventTeamTogether()) {
                  cm.sendOk("One or more instance team members is missing, please wait for them to reach here first.")
                  cm.dispose()
               } else if (cm.hasItem(4001106, 30)) {
                  qty = cm.getItemQuantity(4001106)
                  cm.sendYesNo("Splendid! You have retrieved " + qty + " #t4001106# from this run, now your team will receive the fair amount of EXP from this action. Are you ready to get transported out?")
               } else {
                  cm.sendOk("Your party cannot finish this PQ yet, as you have not reached the minimum of 30 #t4001106#'s in hand yet.")
                  cm.dispose()
               }
            } else {
               cm.sendOk("Let your party leader talk to me to end this quest.")
               cm.dispose()
            }
         } else if (status == 1) {
            cm.removeAll(4001106)
            cm.getEventInstance().giveEventPlayersExp(50 * qty)
            cm.getEventInstance().clearPQ()
            cm.dispose()
         }
      }
   }
}

NPC9103000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9103000(cm: cm))
   }
   return (NPC9103000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }