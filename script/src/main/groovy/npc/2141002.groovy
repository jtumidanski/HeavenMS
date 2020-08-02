package npc
import tools.I18nMessage

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2141002 {
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
         if (mode == 0) {
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
               cm.sendYesNo(I18nMessage.from("2141002_WANT_TO_GET_OUT"))
            } else if (status == 1) {
               cm.warp(270050000, 0)
               cm.dispose()
            }

         } else {
            if (status == 0) {
               cm.sendYesNo(I18nMessage.from("2141002_PINK_BEAN_DEFEATED"))
            } else if (status == 1) {
               if (eim.giveEventReward(cm.getPlayer(), 1)) {
                  cm.warp(270050000)
               } else {
                  cm.sendOk(I18nMessage.from("2141002_NEED_INVENTORY_ROOM"))
               }

               cm.dispose()
            }
         }
      }
   }
}

NPC2141002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2141002(cm: cm))
   }
   return (NPC2141002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }