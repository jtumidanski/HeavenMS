package npc
import tools.I18nMessage

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2102000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031045)) {
         EventManager em = cm.getEventManager("Genie")
         if (em.getProperty("entry") == "true") {
            cm.sendYesNo(I18nMessage.from("2102000_NOT_A_SHORT_FLIGHT"))
         } else {
            cm.sendOk(I18nMessage.from("2102000_READY_FOR_TAKEOFF"))
            cm.dispose()
         }
      } else {
         cm.sendOk(I18nMessage.from("2102000_NEED_A_TICKET"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.sendOk(I18nMessage.from("2102000_IF_YOU_CHANGE_YOUR_MIND"))
         cm.dispose()
         return
      }

      EventManager em = cm.getEventManager("Genie")
      if (em.getProperty("entry") == "true") {
         cm.warp(260000110)
         cm.gainItem(4031045, (short) -1)
      } else {
         cm.sendOk(I18nMessage.from("2102000_HAVE_TO_GO_ON_THE_NEXT_RIDE"))
      }

      cm.dispose()
   }
}

NPC2102000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2102000(cm: cm))
   }
   return (NPC2102000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }