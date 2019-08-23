package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9040001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         cm.dispose()
         return
      }

      if (status == 0) {
         String outText = "It seems you have finished exploring Sharenian Keep, yes? Are you going to return to the recruitment map now?"
         cm.sendYesNo(outText)
      } else if (mode == 1) {
         EventInstanceManager eim = cm.getEventInstance()

         if (eim != null && eim.isEventCleared()) {
            if (!eim.giveEventReward(cm.getPlayer())) {
               cm.sendNext("It seems you don't have a free slot in either your #rEquip#k, #rUse#k or #rEtc#k inventories. Please make some room first.")
            } else {
               cm.warp(101030104)
            }

            cm.dispose()
         } else {
            cm.warp(101030104)
            cm.dispose()
         }
      }
   }
}

NPC9040001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9040001(cm: cm))
   }
   return (NPC9040001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }