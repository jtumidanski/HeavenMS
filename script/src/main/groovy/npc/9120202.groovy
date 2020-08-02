package npc
import tools.I18nMessage

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
               cm.sendYesNo(I18nMessage.from("9120202_ARE_YOU_SURE"))
            } else if (status == 1) {
               cm.warp(801040004, 1)
               cm.dispose()
            }
         } else {
            if (status == 0) {
               cm.sendNext(I18nMessage.from("9120202_YOU_DID_IT"))
            }

            if (status == 1) {
               if (!eim.giveEventReward(cm.getPlayer())) {
                  cm.sendNext(I18nMessage.from("9120202_MAKE_INVENTORY_ROOM"))
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