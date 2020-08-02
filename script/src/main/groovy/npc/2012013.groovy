package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012013 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031074)) {
         EventManager em = cm.getEventManager("Trains")
         if (em.getProperty("entry") == "true") {
            cm.sendYesNo(I18nMessage.from("2012013_GO_TO_LUDIBRIUM"))
         } else {
            cm.sendOk(I18nMessage.from("2012013_ALREADY_TRAVELLING"))
            cm.dispose()
         }
      } else {
         cm.sendOk(I18nMessage.from("2012013_NEED_TICKET"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.sendOk(I18nMessage.from("2012013_OKAY"))
         cm.dispose()
         return
      }
      EventManager em = cm.getEventManager("Trains")
      if (em.getProperty("entry") == "true") {
         cm.warp(200000122)
         cm.gainItem(4031074, (short) -1)
         cm.dispose()
      } else {
         cm.sendOk(I18nMessage.from("2012013_READY_TO_TAKE_OFF_BE_PATIENT"))
         cm.dispose()
      }
   }
}

NPC2012013 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012013(cm: cm))
   }
   return (NPC2012013) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }