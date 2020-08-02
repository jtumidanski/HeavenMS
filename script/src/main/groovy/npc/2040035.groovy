package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

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
            cm.sendNext(I18nMessage.from("2040035_CONGRATULATIONS"))
         } else if (status == 1) {
            EventInstanceManager eim = cm.getEventInstance()

            if (!eim.giveEventReward(cm.getPlayer())) {
               cm.sendNext(I18nMessage.from("2040035_NEED_FREE_SLOT"))
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