package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2041000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031045)) {
         EventManager em = cm.getEventManager("Trains")
         if (em.getProperty("entry") == "true") {
            cm.sendYesNo(I18nMessage.from("2041000_DO_YOU_WANT_TO"))

         } else {
            cm.sendOk(I18nMessage.from("2041000_ALREADY_TRAVELLING"))

            cm.dispose()
         }
      } else {
         cm.sendOk(I18nMessage.from("2041000_NEED_TICKET"))

         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.sendOk(I18nMessage.from("2041000_TALK_TO_ME"))

         cm.dispose()
         return
      }

      EventManager em = cm.getEventManager("Trains")
      if (em.getProperty("entry") == "true") {
         cm.warp(220000111)
         cm.gainItem(4031045, (short) -1)
         cm.dispose()
      } else {
         cm.sendOk(I18nMessage.from("2041000_BE_PATIENT"))

         cm.dispose()
      }
   }
}

NPC2041000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2041000(cm: cm))
   }
   return (NPC2041000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }