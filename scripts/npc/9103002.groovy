package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9103002 {
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
      }
      if (mode == 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendYesNo("Your party gave a stellar effort and gathered up at least 30 coupons. For that, I have a present for each and every one of you. After receiving the present, you will be sent back to Ludibrium. Now, would you like to receive the present right now?")
         } else if (status == 1) {
            EventInstanceManager eim = cm.getEventInstance()
            if (!eim.giveEventReward(cm.getPlayer())) {
               cm.sendNext("It seems you don't have a free slot in either your #rEquip#k, #rUse#k or #rEtc#k inventories. Please make some room and try again.")
            } else {
               cm.warp(809050017)
            }

            cm.dispose()
         }
      }
   }
}

NPC9103002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9103002(cm: cm))
   }
   return (NPC9103002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }