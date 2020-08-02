package npc
import tools.I18nMessage

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2112018 {
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

         if (status == 0) {
            if (eim.getIntProperty("escortFail") == 1) {
               cm.sendNext(I18nMessage.from("2112018_THANK_YOU"))
            } else {
               cm.sendNext(I18nMessage.from("2112018_THANK_YOU_LONG"))
            }
         } else {
            if (eim.giveEventReward(cm.getPlayer())) {
               cm.warp((eim.getIntProperty("isAlcadno") == 0) ? 261000011 : 261000021)
            } else {
               cm.sendOk(I18nMessage.from("2112018_FREE_A_SLOT"))
            }

            cm.dispose()
         }
      }
   }
}

NPC2112018 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2112018(cm: cm))
   }
   return (NPC2112018) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }