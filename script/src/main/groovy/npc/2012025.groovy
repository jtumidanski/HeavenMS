package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012025 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031576)) {
         EventManager em = cm.getEventManager("Genie")
         if (em.getProperty("entry") == "true") {
            cm.sendYesNo(I18nMessage.from("2012025_DO_YOU_STILL_WISH_TO_BOARD"))
         } else {
            cm.sendOk(I18nMessage.from("2012025_BE_PATIENT"))
            cm.dispose()
         }
      } else {
         cm.sendOk(I18nMessage.from("2012025_NEED_TICKET"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.sendOk(I18nMessage.from("2012025_OKAY"))
         cm.dispose()
         return
      }

      EventManager em = cm.getEventManager("Genie")
      if (em.getProperty("entry") == "true") {
         cm.warp(200000152)
         cm.gainItem(4031576, (short) -1)
      } else {
         cm.sendOk(I18nMessage.from("2012025_BE_PATIENT"))
      }

      cm.dispose()
   }
}

NPC2012025 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012025(cm: cm))
   }
   return (NPC2012025) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }