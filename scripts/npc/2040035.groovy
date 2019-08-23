package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Arturo
	Map(s): 		Abandoned Tower <Determine to Adventure>
	Description: 	
*/


class NPC2040035 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0 && mode == 1) {
            cm.sendNext("Congratulations on sealing the dimensional crack! For all of your hard work, I have a gift for you! Here take this prize.")
         } else if (status == 1) {
            EventInstanceManager eim = cm.getEventInstance()

            if (!eim.giveEventReward(cm.getPlayer())) {
               cm.sendNext("It seems you don't have a free slot in either your #rEquip#k, #rUse#k or #rEtc#k inventories. Please make some room and try again.")
            } else {
               cm.warp(221024500)
            }

            cm.dispose()
         }
      }
   }
}

NPC2040035 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040035(cm: cm))
   }
   return (NPC2040035) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }