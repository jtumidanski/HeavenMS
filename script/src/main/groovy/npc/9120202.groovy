package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120202 {
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

         EventInstanceManager eim = cm.getEventInstance()
         if (!eim.isEventCleared()) {
            if (status == 0) {
               cm.sendYesNo("If you leave now, you won't be able to return. Are you sure you want to leave?")
            } else if (status == 1) {
               cm.warp(801040004, 1)
               cm.dispose()
            }
         } else {
            if (status == 0) {
               cm.sendNext("You guys did it, great job! Now our city is free from the tyranny of their mobs! As representative of the city, please accept this as a prize for your efforts, as I get you back to town.")
            }

            if (status == 1) {
               if (!eim.giveEventReward(cm.getPlayer())) {
                  cm.sendNext("Please make room on your inventory first...")
               } else {
                  cm.warp(801040101)
               }

               cm.dispose()
            }
         }
      }
   }
}

NPC9120202 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120202(cm: cm))
   }
   return (NPC9120202) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }